#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: GPL-2.0-only
#

import re
import filecmp
import shutil
from oe.rootfs import Rootfs
from oe.manifest import Manifest
from oe.utils import execute_pre_post_process
from oe.package_manager.ipk.manifest import PkgManifest
from oe.package_manager.ipk import OpkgPM

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
        self.log_check_regex = '(exit 1|Collected errors)'

        self.manifest = PkgManifest(d, manifest_dir)
        self.opkg_conf = self.d.getVar("IPKGCONF_TARGET")
        self.pkg_archs = self.d.getVar("ALL_MULTILIB_PACKAGE_ARCHS")

        self.inc_opkg_image_gen = self.d.getVar('INC_IPK_IMAGE_GEN') or ""
        if self._remove_old_rootfs():
            bb.utils.remove(self.image_rootfs, True)
            self.pm = OpkgPM(d,
                             self.image_rootfs,
                             self.opkg_conf,
                             self.pkg_archs)
        else:
            self.pm = OpkgPM(d,
                             self.image_rootfs,
                             self.opkg_conf,
                             self.pkg_archs)
            self.pm.recover_packaging_data()

        bb.utils.remove(self.d.getVar('MULTILIB_TEMP_ROOTFS'), True)
    '''
    Compare two files with the same key twice to see if they are equal.
    If they are not equal, it means they are duplicated and come from
    different packages.
    '''
    def _file_equal(self, key, f1, f2):
        if filecmp.cmp(f1, f2):
            return True
        # Not equal
        return False

    """
    This function was reused from the old implementation.
    See commit: "image.bbclass: Added variables for multilib support." by
    Lianhao Lu.
    """
    def _multilib_sanity_test(self, dirs):

        allow_replace = "|".join((self.d.getVar("MULTILIBRE_ALLOW_REP") or "").split())
        if allow_replace is None:
            allow_replace = ""

        allow_rep = re.compile(re.sub(r"\|$", r"", allow_replace))
        error_prompt = "Multilib check error:"

        files = {}
        for dir in dirs:
            for root, subfolders, subfiles in os.walk(dir):
                for file in subfiles:
                    item = os.path.join(root, file)
                    key = str(os.path.join("/", os.path.relpath(item, dir)))

                    valid = True
                    if key in files:
                        #check whether the file is allow to replace
                        if allow_rep.match(key):
                            valid = True
                        else:
                            if os.path.exists(files[key]) and \
                               os.path.exists(item) and \
                               not self._file_equal(key, files[key], item):
                                valid = False
                                bb.fatal("%s duplicate files %s %s is not the same\n" %
                                         (error_prompt, item, files[key]))

                    #pass the check, add to list
                    if valid:
                        files[key] = item

    def _multilib_test_install(self, pkgs):
        ml_temp = self.d.getVar("MULTILIB_TEMP_ROOTFS")
        bb.utils.mkdirhier(ml_temp)

        dirs = [self.image_rootfs]

        for variant in self.d.getVar("MULTILIB_VARIANTS").split():
            ml_target_rootfs = os.path.join(ml_temp, variant)

            bb.utils.remove(ml_target_rootfs, True)

            ml_opkg_conf = os.path.join(ml_temp,
                                        variant + "-" + os.path.basename(self.opkg_conf))

            ml_pm = OpkgPM(self.d, ml_target_rootfs, ml_opkg_conf, self.pkg_archs, prepare_index=False)

            ml_pm.update()
            ml_pm.install(pkgs)

            dirs.append(ml_target_rootfs)

        self._multilib_sanity_test(dirs)

    '''
    While ipk incremental image generation is enabled, it will remove the
    unneeded pkgs by comparing the old full manifest in previous existing
    image and the new full manifest in the current image.
    '''
    def _remove_extra_packages(self, pkgs_initial_install):
        if self.inc_opkg_image_gen == "1":
            # Parse full manifest in previous existing image creation session
            old_full_manifest = self.manifest.parse_full_manifest()

            # Create full manifest for the current image session, the old one
            # will be replaced by the new one.
            self.manifest.create_full(self.pm)

            # Parse full manifest in current image creation session
            new_full_manifest = self.manifest.parse_full_manifest()

            pkg_to_remove = list()
            for pkg in old_full_manifest:
                if pkg not in new_full_manifest:
                    pkg_to_remove.append(pkg)

            if pkg_to_remove != []:
                bb.note('decremental removed: %s' % ' '.join(pkg_to_remove))
                self.pm.remove(pkg_to_remove)

    '''
    Compare with previous existing image creation, if some conditions
    triggered, the previous old image should be removed.
    The conditions include any of 'PACKAGE_EXCLUDE, NO_RECOMMENDATIONS
    and BAD_RECOMMENDATIONS' has been changed.
    '''
    def _remove_old_rootfs(self):
        if self.inc_opkg_image_gen != "1":
            return True

        vars_list_file = self.d.expand('${T}/vars_list')

        old_vars_list = ""
        if os.path.exists(vars_list_file):
            old_vars_list = open(vars_list_file, 'r+').read()

        new_vars_list = '%s:%s:%s\n' % \
                ((self.d.getVar('BAD_RECOMMENDATIONS') or '').strip(),
                 (self.d.getVar('NO_RECOMMENDATIONS') or '').strip(),
                 (self.d.getVar('PACKAGE_EXCLUDE') or '').strip())
        open(vars_list_file, 'w+').write(new_vars_list)

        if old_vars_list != new_vars_list:
            return True

        return False

    def _create(self):
        pkgs_to_install = self.manifest.parse_initial_manifest()
        opkg_pre_process_cmds = self.d.getVar('OPKG_PREPROCESS_COMMANDS')
        opkg_post_process_cmds = self.d.getVar('OPKG_POSTPROCESS_COMMANDS')

        # update PM index files
        self.pm.write_index()

        execute_pre_post_process(self.d, opkg_pre_process_cmds)

        if self.progress_reporter:
            self.progress_reporter.next_stage()
            # Steps are a bit different in order, skip next
            self.progress_reporter.next_stage()

        self.pm.update()

        if self.progress_reporter:
            self.progress_reporter.next_stage()

        if self.inc_opkg_image_gen == "1":
            self._remove_extra_packages(pkgs_to_install)

        if self.progress_reporter:
            self.progress_reporter.next_stage()

        for pkg_type in self.install_order:
            if pkg_type in pkgs_to_install:
                # For multilib, we perform a sanity test before final install
                # If sanity test fails, it will automatically do a bb.fatal()
                # and the installation will stop
                if pkg_type == Manifest.PKG_TYPE_MULTILIB:
                    self._multilib_test_install(pkgs_to_install[pkg_type])

                self.pm.install(pkgs_to_install[pkg_type],
                                [False, True][pkg_type == Manifest.PKG_TYPE_ATTEMPT_ONLY])

        if self.progress_reporter:
            self.progress_reporter.next_stage()

        self.pm.install_complementary()

        if self.progress_reporter:
            self.progress_reporter.next_stage()

        opkg_lib_dir = self.d.getVar('OPKGLIBDIR')
        opkg_dir = os.path.join(opkg_lib_dir, 'opkg')
        self._setup_dbg_rootfs([opkg_dir])

        execute_pre_post_process(self.d, opkg_post_process_cmds)

        if self.inc_opkg_image_gen == "1":
            self.pm.backup_packaging_data()

        if self.progress_reporter:
            self.progress_reporter.next_stage()

    @staticmethod
    def _depends_list():
        return ['IPKGCONF_SDK', 'IPK_FEED_URIS', 'DEPLOY_DIR_IPK', 'IPKGCONF_TARGET', 'INC_IPK_IMAGE_GEN', 'OPKG_ARGS', 'OPKGLIBDIR', 'OPKG_PREPROCESS_COMMANDS', 'OPKG_POSTPROCESS_COMMANDS', 'OPKGLIBDIR']

    def _get_delayed_postinsts(self):
        status_file = os.path.join(self.image_rootfs,
                                   self.d.getVar('OPKGLIBDIR').strip('/'),
                                   "opkg", "status")
        return self._get_delayed_postinsts_common(status_file)

    def _save_postinsts(self):
        dst_postinst_dir = self.d.expand("${IMAGE_ROOTFS}${sysconfdir}/ipk-postinsts")
        src_postinst_dir = self.d.expand("${IMAGE_ROOTFS}${OPKGLIBDIR}/opkg/info")
        return self._save_postinsts_common(dst_postinst_dir, src_postinst_dir)

    def _log_check(self):
        self._log_check_warn()
        self._log_check_error()

    def _cleanup(self):
        self.pm.remove_lists()
