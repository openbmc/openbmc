#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: GPL-2.0-only
#

from oe.rootfs import Rootfs
from oe.manifest import Manifest
from oe.utils import execute_pre_post_process
from oe.package_manager.rpm.manifest import PkgManifest
from oe.package_manager.rpm import RpmPM

class PkgRootfs(Rootfs):
    def __init__(self, d, manifest_dir, progress_reporter=None, logcatcher=None):
        super(PkgRootfs, self).__init__(d, progress_reporter, logcatcher)
        self.log_check_regex = r'(unpacking of archive failed|Cannot find package'\
                               r'|exit 1|ERROR: |Error: |Error |ERROR '\
                               r'|Failed |Failed: |Failed$|Failed\(\d+\):)'

        self.manifest = PkgManifest(d, manifest_dir)

        self.pm = RpmPM(d,
                        d.getVar('IMAGE_ROOTFS'),
                        self.d.getVar('TARGET_VENDOR')
                        )

        self.inc_rpm_image_gen = self.d.getVar('INC_RPM_IMAGE_GEN')
        if self.inc_rpm_image_gen != "1":
            bb.utils.remove(self.image_rootfs, True)
        else:
            self.pm.recovery_packaging_data()
        bb.utils.remove(self.d.getVar('MULTILIB_TEMP_ROOTFS'), True)

        self.pm.create_configs()

    '''
    While rpm incremental image generation is enabled, it will remove the
    unneeded pkgs by comparing the new install solution manifest and the
    old installed manifest.
    '''
    def _create_incremental(self, pkgs_initial_install):
        if self.inc_rpm_image_gen == "1":

            pkgs_to_install = list()
            for pkg_type in pkgs_initial_install:
                pkgs_to_install += pkgs_initial_install[pkg_type]

            installed_manifest = self.pm.load_old_install_solution()
            solution_manifest = self.pm.dump_install_solution(pkgs_to_install)

            pkg_to_remove = list()
            for pkg in installed_manifest:
                if pkg not in solution_manifest:
                    pkg_to_remove.append(pkg)

            self.pm.update()

            bb.note('incremental update -- upgrade packages in place ')
            self.pm.upgrade()
            if pkg_to_remove != []:
                bb.note('incremental removed: %s' % ' '.join(pkg_to_remove))
                self.pm.remove(pkg_to_remove)

            self.pm.autoremove()

    def _create(self):
        pkgs_to_install = self.manifest.parse_initial_manifest()
        rpm_pre_process_cmds = self.d.getVar('RPM_PREPROCESS_COMMANDS')
        rpm_post_process_cmds = self.d.getVar('RPM_POSTPROCESS_COMMANDS')

        # update PM index files
        self.pm.write_index()

        execute_pre_post_process(self.d, rpm_pre_process_cmds)

        if self.progress_reporter:
            self.progress_reporter.next_stage()

        if self.inc_rpm_image_gen == "1":
            self._create_incremental(pkgs_to_install)

        if self.progress_reporter:
            self.progress_reporter.next_stage()

        self.pm.update()

        pkgs = []
        pkgs_attempt = []
        for pkg_type in pkgs_to_install:
            if pkg_type == Manifest.PKG_TYPE_ATTEMPT_ONLY:
                pkgs_attempt += pkgs_to_install[pkg_type]
            else:
                pkgs += pkgs_to_install[pkg_type]

        if self.progress_reporter:
            self.progress_reporter.next_stage()

        self.pm.install(pkgs)

        if self.progress_reporter:
            self.progress_reporter.next_stage()

        self.pm.install(pkgs_attempt, True)

        if self.progress_reporter:
            self.progress_reporter.next_stage()

        self.pm.install_complementary()

        if self.progress_reporter:
            self.progress_reporter.next_stage()

        self._setup_dbg_rootfs(['/etc/rpm', '/etc/rpmrc', '/etc/dnf', '/var/lib/rpm', '/var/cache/dnf', '/var/lib/dnf'])

        execute_pre_post_process(self.d, rpm_post_process_cmds)

        if self.inc_rpm_image_gen == "1":
            self.pm.backup_packaging_data()

        if self.progress_reporter:
            self.progress_reporter.next_stage()


    @staticmethod
    def _depends_list():
        return ['DEPLOY_DIR_RPM', 'INC_RPM_IMAGE_GEN', 'RPM_PREPROCESS_COMMANDS',
                'RPM_POSTPROCESS_COMMANDS', 'RPM_PREFER_ELF_ARCH']

    def _get_delayed_postinsts(self):
        postinst_dir = self.d.expand("${IMAGE_ROOTFS}${sysconfdir}/rpm-postinsts")
        if os.path.isdir(postinst_dir):
            files = os.listdir(postinst_dir)
            for f in files:
                bb.note('Delayed package scriptlet: %s' % f)
            return files

        return None

    def _save_postinsts(self):
        # this is just a stub. For RPM, the failed postinstalls are
        # already saved in /etc/rpm-postinsts
        pass

    def _log_check(self):
        self._log_check_warn()
        self._log_check_error()

    def _cleanup(self):
        if bb.utils.contains("IMAGE_FEATURES", "package-management", True, False, self.d):
            self.pm._invoke_dnf(["clean", "all"])
