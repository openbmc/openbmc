#
# SPDX-License-Identifier: GPL-2.0-only
#

import re
import shutil
from oe.rootfs import Rootfs
from oe.manifest import Manifest
from oe.utils import execute_pre_post_process
from oe.package_manager.deb.manifest import PkgManifest
from oe.package_manager.deb import DpkgPM

class DpkgOpkgRootfs(Rootfs):
    def __init__(self, d, progress_reporter=None, logcatcher=None):
        super(DpkgOpkgRootfs, self).__init__(d, progress_reporter, logcatcher)

    def _get_pkgs_postinsts(self, status_file):
        def _get_pkg_depends_list(pkg_depends):
            pkg_depends_list = []
            # filter version requirements like libc (>= 1.1)
            for dep in pkg_depends.split(', '):
                m_dep = re.match(r"^(.*) \(.*\)$", dep)
                if m_dep:
                    dep = m_dep.group(1)
                pkg_depends_list.append(dep)

            return pkg_depends_list

        pkgs = {}
        pkg_name = ""
        pkg_status_match = False
        pkg_depends = ""

        with open(status_file) as status:
            data = status.read()
            status.close()
            for line in data.split('\n'):
                m_pkg = re.match(r"^Package: (.*)", line)
                m_status = re.match(r"^Status:.*unpacked", line)
                m_depends = re.match(r"^Depends: (.*)", line)

                #Only one of m_pkg, m_status or m_depends is not None at time
                #If m_pkg is not None, we started a new package
                if m_pkg is not None:
                    #Get Package name
                    pkg_name = m_pkg.group(1)
                    #Make sure we reset other variables
                    pkg_status_match = False
                    pkg_depends = ""
                elif m_status is not None:
                    #New status matched
                    pkg_status_match = True
                elif m_depends is not None:
                    #New depends macthed
                    pkg_depends = m_depends.group(1)
                else:
                    pass

                #Now check if we can process package depends and postinst
                if "" != pkg_name and pkg_status_match:
                    pkgs[pkg_name] = _get_pkg_depends_list(pkg_depends)
                else:
                    #Not enough information
                    pass

        # remove package dependencies not in postinsts
        pkg_names = list(pkgs.keys())
        for pkg_name in pkg_names:
            deps = pkgs[pkg_name][:]

            for d in deps:
                if d not in pkg_names:
                    pkgs[pkg_name].remove(d)

        return pkgs

    def _get_delayed_postinsts_common(self, status_file):
        def _dep_resolve(graph, node, resolved, seen):
            seen.append(node)

            for edge in graph[node]:
                if edge not in resolved:
                    if edge in seen:
                        raise RuntimeError("Packages %s and %s have " \
                                "a circular dependency in postinsts scripts." \
                                % (node, edge))
                    _dep_resolve(graph, edge, resolved, seen)

            resolved.append(node)

        pkg_list = []

        pkgs = None
        if not self.d.getVar('PACKAGE_INSTALL').strip():
            bb.note("Building empty image")
        else:
            pkgs = self._get_pkgs_postinsts(status_file)
        if pkgs:
            root = "__packagegroup_postinst__"
            pkgs[root] = list(pkgs.keys())
            _dep_resolve(pkgs, root, pkg_list, [])
            pkg_list.remove(root)

        if len(pkg_list) == 0:
            return None

        return pkg_list

    def _save_postinsts_common(self, dst_postinst_dir, src_postinst_dir):
        if bb.utils.contains("IMAGE_FEATURES", "package-management",
                         True, False, self.d):
            return
        num = 0
        for p in self._get_delayed_postinsts():
            bb.utils.mkdirhier(dst_postinst_dir)

            if os.path.exists(os.path.join(src_postinst_dir, p + ".postinst")):
                shutil.copy(os.path.join(src_postinst_dir, p + ".postinst"),
                            os.path.join(dst_postinst_dir, "%03d-%s" % (num, p)))

            num += 1

class PkgRootfs(DpkgOpkgRootfs):
    def __init__(self, d, manifest_dir, progress_reporter=None, logcatcher=None):
        super(PkgRootfs, self).__init__(d, progress_reporter, logcatcher)
        self.log_check_regex = '^E:'
        self.log_check_expected_regexes = \
        [
            "^E: Unmet dependencies."
        ]

        bb.utils.remove(self.image_rootfs, True)
        bb.utils.remove(self.d.getVar('MULTILIB_TEMP_ROOTFS'), True)
        self.manifest = PkgManifest(d, manifest_dir)
        self.pm = DpkgPM(d, d.getVar('IMAGE_ROOTFS'),
                         d.getVar('PACKAGE_ARCHS'),
                         d.getVar('DPKG_ARCH'))


    def _create(self):
        pkgs_to_install = self.manifest.parse_initial_manifest()
        deb_pre_process_cmds = self.d.getVar('DEB_PREPROCESS_COMMANDS')
        deb_post_process_cmds = self.d.getVar('DEB_POSTPROCESS_COMMANDS')

        alt_dir = self.d.expand("${IMAGE_ROOTFS}/var/lib/dpkg/alternatives")
        bb.utils.mkdirhier(alt_dir)

        # update PM index files
        self.pm.write_index()

        execute_pre_post_process(self.d, deb_pre_process_cmds)

        if self.progress_reporter:
            self.progress_reporter.next_stage()
            # Don't support incremental, so skip that
            self.progress_reporter.next_stage()

        self.pm.update()

        if self.progress_reporter:
            self.progress_reporter.next_stage()

        for pkg_type in self.install_order:
            if pkg_type in pkgs_to_install:
                self.pm.install(pkgs_to_install[pkg_type],
                                [False, True][pkg_type == Manifest.PKG_TYPE_ATTEMPT_ONLY])
                self.pm.fix_broken_dependencies()

        if self.progress_reporter:
            # Don't support attemptonly, so skip that
            self.progress_reporter.next_stage()
            self.progress_reporter.next_stage()

        self.pm.install_complementary()

        if self.progress_reporter:
            self.progress_reporter.next_stage()

        self._setup_dbg_rootfs(['/var/lib/dpkg'])

        self.pm.fix_broken_dependencies()

        self.pm.mark_packages("installed")

        self.pm.run_pre_post_installs()

        execute_pre_post_process(self.d, deb_post_process_cmds)

        if self.progress_reporter:
            self.progress_reporter.next_stage()

    @staticmethod
    def _depends_list():
        return ['DEPLOY_DIR_DEB', 'DEB_SDK_ARCH', 'APTCONF_TARGET', 'APT_ARGS', 'DPKG_ARCH', 'DEB_PREPROCESS_COMMANDS', 'DEB_POSTPROCESS_COMMANDS']

    def _get_delayed_postinsts(self):
        status_file = self.image_rootfs + "/var/lib/dpkg/status"
        return self._get_delayed_postinsts_common(status_file)

    def _save_postinsts(self):
        dst_postinst_dir = self.d.expand("${IMAGE_ROOTFS}${sysconfdir}/deb-postinsts")
        src_postinst_dir = self.d.expand("${IMAGE_ROOTFS}/var/lib/dpkg/info")
        return self._save_postinsts_common(dst_postinst_dir, src_postinst_dir)

    def _log_check(self):
        self._log_check_warn()
        self._log_check_error()

    def _cleanup(self):
        pass
