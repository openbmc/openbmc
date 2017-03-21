from abc import ABCMeta, abstractmethod
from oe.utils import execute_pre_post_process
from oe.package_manager import *
from oe.manifest import *
import oe.path
import filecmp
import shutil
import os
import subprocess
import re


class Rootfs(object, metaclass=ABCMeta):
    """
    This is an abstract class. Do not instantiate this directly.
    """

    def __init__(self, d, progress_reporter=None):
        self.d = d
        self.pm = None
        self.image_rootfs = self.d.getVar('IMAGE_ROOTFS', True)
        self.deploydir = self.d.getVar('IMGDEPLOYDIR', True)
        self.progress_reporter = progress_reporter

        self.install_order = Manifest.INSTALL_ORDER

    @abstractmethod
    def _create(self):
        pass

    @abstractmethod
    def _get_delayed_postinsts(self):
        pass

    @abstractmethod
    def _save_postinsts(self):
        pass

    @abstractmethod
    def _log_check(self):
        pass

    def _log_check_common(self, type, match):
        # Ignore any lines containing log_check to avoid recursion, and ignore
        # lines beginning with a + since sh -x may emit code which isn't
        # actually executed, but may contain error messages
        excludes = [ 'log_check', r'^\+' ]
        if hasattr(self, 'log_check_expected_regexes'):
            excludes.extend(self.log_check_expected_regexes)
        excludes = [re.compile(x) for x in excludes]
        r = re.compile(match)
        log_path = self.d.expand("${T}/log.do_rootfs")
        messages = []
        with open(log_path, 'r') as log:
            for line in log:
                for ee in excludes:
                    m = ee.search(line)
                    if m:
                        break
                if m:
                    continue

                m = r.search(line)
                if m:
                    messages.append('[log_check] %s' % line)
        if messages:
            if len(messages) == 1:
                msg = '1 %s message' % type
            else:
                msg = '%d %s messages' % (len(messages), type)
            msg = '[log_check] %s: found %s in the logfile:\n%s' % \
                (self.d.getVar('PN', True), msg, ''.join(messages))
            if type == 'error':
                bb.fatal(msg)
            else:
                bb.warn(msg)

    def _log_check_warn(self):
        self._log_check_common('warning', '^(warn|Warn|WARNING:)')

    def _log_check_error(self):
        self._log_check_common('error', self.log_check_regex)

    def _insert_feed_uris(self):
        if bb.utils.contains("IMAGE_FEATURES", "package-management",
                         True, False, self.d):
            self.pm.insert_feeds_uris()

    @abstractmethod
    def _handle_intercept_failure(self, failed_script):
        pass

    """
    The _cleanup() method should be used to clean-up stuff that we don't really
    want to end up on target. For example, in the case of RPM, the DB locks.
    The method is called, once, at the end of create() method.
    """
    @abstractmethod
    def _cleanup(self):
        pass

    def _setup_dbg_rootfs(self, dirs):
        gen_debugfs = self.d.getVar('IMAGE_GEN_DEBUGFS', True) or '0'
        if gen_debugfs != '1':
           return

        bb.note("  Renaming the original rootfs...")
        try:
            shutil.rmtree(self.image_rootfs + '-orig')
        except:
            pass
        os.rename(self.image_rootfs, self.image_rootfs + '-orig')

        bb.note("  Creating debug rootfs...")
        bb.utils.mkdirhier(self.image_rootfs)

        bb.note("  Copying back package database...")
        for dir in dirs:
            if not os.path.isdir(self.image_rootfs + '-orig' + dir):
                continue
            bb.utils.mkdirhier(self.image_rootfs + os.path.dirname(dir))
            shutil.copytree(self.image_rootfs + '-orig' + dir, self.image_rootfs + dir, symlinks=True)

        cpath = oe.cachedpath.CachedPath()
        # Copy files located in /usr/lib/debug or /usr/src/debug
        for dir in ["/usr/lib/debug", "/usr/src/debug"]:
            src = self.image_rootfs + '-orig' + dir
            if cpath.exists(src):
                dst = self.image_rootfs + dir
                bb.utils.mkdirhier(os.path.dirname(dst))
                shutil.copytree(src, dst)

        # Copy files with suffix '.debug' or located in '.debug' dir.
        for root, dirs, files in cpath.walk(self.image_rootfs + '-orig'):
            relative_dir = root[len(self.image_rootfs + '-orig'):]
            for f in files:
                if f.endswith('.debug') or '/.debug' in relative_dir:
                    bb.utils.mkdirhier(self.image_rootfs + relative_dir)
                    shutil.copy(os.path.join(root, f),
                                self.image_rootfs + relative_dir)

        bb.note("  Install complementary '*-dbg' packages...")
        self.pm.install_complementary('*-dbg')

        bb.note("  Rename debug rootfs...")
        try:
            shutil.rmtree(self.image_rootfs + '-dbg')
        except:
            pass
        os.rename(self.image_rootfs, self.image_rootfs + '-dbg')

        bb.note("  Restoreing original rootfs...")
        os.rename(self.image_rootfs + '-orig', self.image_rootfs)

    def _exec_shell_cmd(self, cmd):
        fakerootcmd = self.d.getVar('FAKEROOT', True)
        if fakerootcmd is not None:
            exec_cmd = [fakerootcmd, cmd]
        else:
            exec_cmd = cmd

        try:
            subprocess.check_output(exec_cmd, stderr=subprocess.STDOUT)
        except subprocess.CalledProcessError as e:
            return("Command '%s' returned %d:\n%s" % (e.cmd, e.returncode, e.output))

        return None

    def create(self):
        bb.note("###### Generate rootfs #######")
        pre_process_cmds = self.d.getVar("ROOTFS_PREPROCESS_COMMAND", True)
        post_process_cmds = self.d.getVar("ROOTFS_POSTPROCESS_COMMAND", True)
        rootfs_post_install_cmds = self.d.getVar('ROOTFS_POSTINSTALL_COMMAND', True)

        postinst_intercepts_dir = self.d.getVar("POSTINST_INTERCEPTS_DIR", True)
        if not postinst_intercepts_dir:
            postinst_intercepts_dir = self.d.expand("${COREBASE}/scripts/postinst-intercepts")
        intercepts_dir = os.path.join(self.d.getVar('WORKDIR', True),
                                      "intercept_scripts")

        bb.utils.remove(intercepts_dir, True)

        bb.utils.mkdirhier(self.image_rootfs)

        bb.utils.mkdirhier(self.deploydir)

        shutil.copytree(postinst_intercepts_dir, intercepts_dir)

        shutil.copy(self.d.expand("${COREBASE}/meta/files/deploydir_readme.txt"),
                    self.deploydir +
                    "/README_-_DO_NOT_DELETE_FILES_IN_THIS_DIRECTORY.txt")

        execute_pre_post_process(self.d, pre_process_cmds)

        if self.progress_reporter:
            self.progress_reporter.next_stage()

        # call the package manager dependent create method
        self._create()

        sysconfdir = self.image_rootfs + self.d.getVar('sysconfdir', True)
        bb.utils.mkdirhier(sysconfdir)
        with open(sysconfdir + "/version", "w+") as ver:
            ver.write(self.d.getVar('BUILDNAME', True) + "\n")

        execute_pre_post_process(self.d, rootfs_post_install_cmds)

        self._run_intercepts()

        execute_pre_post_process(self.d, post_process_cmds)

        if self.progress_reporter:
            self.progress_reporter.next_stage()

        if bb.utils.contains("IMAGE_FEATURES", "read-only-rootfs",
                         True, False, self.d):
            delayed_postinsts = self._get_delayed_postinsts()
            if delayed_postinsts is not None:
                bb.fatal("The following packages could not be configured "
                         "offline and rootfs is read-only: %s" %
                         delayed_postinsts)

        if self.d.getVar('USE_DEVFS', True) != "1":
            self._create_devfs()

        self._uninstall_unneeded()

        if self.progress_reporter:
            self.progress_reporter.next_stage()

        self._insert_feed_uris()

        self._run_ldconfig()

        if self.d.getVar('USE_DEPMOD', True) != "0":
            self._generate_kernel_module_deps()

        self._cleanup()
        self._log_check()

        if self.progress_reporter:
            self.progress_reporter.next_stage()


    def _uninstall_unneeded(self):
        # Remove unneeded init script symlinks
        delayed_postinsts = self._get_delayed_postinsts()
        if delayed_postinsts is None:
            if os.path.exists(self.d.expand("${IMAGE_ROOTFS}${sysconfdir}/init.d/run-postinsts")):
                self._exec_shell_cmd(["update-rc.d", "-f", "-r",
                                      self.d.getVar('IMAGE_ROOTFS', True),
                                      "run-postinsts", "remove"])

        image_rorfs = bb.utils.contains("IMAGE_FEATURES", "read-only-rootfs",
                                        True, False, self.d)
        image_rorfs_force = self.d.getVar('FORCE_RO_REMOVE', True)

        if image_rorfs or image_rorfs_force == "1":
            # Remove components that we don't need if it's a read-only rootfs
            unneeded_pkgs = self.d.getVar("ROOTFS_RO_UNNEEDED", True).split()
            pkgs_installed = image_list_installed_packages(self.d)
            pkgs_to_remove = [pkg for pkg in pkgs_installed if pkg in unneeded_pkgs]

            if len(pkgs_to_remove) > 0:
                self.pm.remove(pkgs_to_remove, False)

        if delayed_postinsts:
            self._save_postinsts()
            if image_rorfs:
                bb.warn("There are post install scripts "
                        "in a read-only rootfs")

        post_uninstall_cmds = self.d.getVar("ROOTFS_POSTUNINSTALL_COMMAND", True)
        execute_pre_post_process(self.d, post_uninstall_cmds)

        runtime_pkgmanage = bb.utils.contains("IMAGE_FEATURES", "package-management",
                                              True, False, self.d)
        if not runtime_pkgmanage:
            # Remove the package manager data files
            self.pm.remove_packaging_data()

    def _run_intercepts(self):
        intercepts_dir = os.path.join(self.d.getVar('WORKDIR', True),
                                      "intercept_scripts")

        bb.note("Running intercept scripts:")
        os.environ['D'] = self.image_rootfs
        os.environ['STAGING_DIR_NATIVE'] = self.d.getVar('STAGING_DIR_NATIVE', True)
        for script in os.listdir(intercepts_dir):
            script_full = os.path.join(intercepts_dir, script)

            if script == "postinst_intercept" or not os.access(script_full, os.X_OK):
                continue

            bb.note("> Executing %s intercept ..." % script)

            try:
                subprocess.check_call(script_full)
            except subprocess.CalledProcessError as e:
                bb.warn("The postinstall intercept hook '%s' failed (exit code: %d)! See log for details!" %
                        (script, e.returncode))

                with open(script_full) as intercept:
                    registered_pkgs = None
                    for line in intercept.read().split("\n"):
                        m = re.match("^##PKGS:(.*)", line)
                        if m is not None:
                            registered_pkgs = m.group(1).strip()
                            break

                    if registered_pkgs is not None:
                        bb.warn("The postinstalls for the following packages "
                                "will be postponed for first boot: %s" %
                                registered_pkgs)

                        # call the backend dependent handler
                        self._handle_intercept_failure(registered_pkgs)

    def _run_ldconfig(self):
        if self.d.getVar('LDCONFIGDEPEND', True):
            bb.note("Executing: ldconfig -r" + self.image_rootfs + "-c new -v")
            self._exec_shell_cmd(['ldconfig', '-r', self.image_rootfs, '-c',
                                  'new', '-v'])

    def _check_for_kernel_modules(self, modules_dir):
        for root, dirs, files in os.walk(modules_dir, topdown=True):
            for name in files:
                found_ko = name.endswith(".ko")
                if found_ko:
                    return found_ko
        return False

    def _generate_kernel_module_deps(self):
        modules_dir = os.path.join(self.image_rootfs, 'lib', 'modules')
        # if we don't have any modules don't bother to do the depmod
        if not self._check_for_kernel_modules(modules_dir):
            bb.note("No Kernel Modules found, not running depmod")
            return

        kernel_abi_ver_file = oe.path.join(self.d.getVar('PKGDATA_DIR', True), "kernel-depmod",
                                           'kernel-abiversion')
        if not os.path.exists(kernel_abi_ver_file):
            bb.fatal("No kernel-abiversion file found (%s), cannot run depmod, aborting" % kernel_abi_ver_file)

        kernel_ver = open(kernel_abi_ver_file).read().strip(' \n')
        versioned_modules_dir = os.path.join(self.image_rootfs, modules_dir, kernel_ver)

        bb.utils.mkdirhier(versioned_modules_dir)

        self._exec_shell_cmd(['depmodwrapper', '-a', '-b', self.image_rootfs, kernel_ver])

    """
    Create devfs:
    * IMAGE_DEVICE_TABLE is the old name to an absolute path to a device table file
    * IMAGE_DEVICE_TABLES is a new name for a file, or list of files, seached
      for in the BBPATH
    If neither are specified then the default name of files/device_table-minimal.txt
    is searched for in the BBPATH (same as the old version.)
    """
    def _create_devfs(self):
        devtable_list = []
        devtable = self.d.getVar('IMAGE_DEVICE_TABLE', True)
        if devtable is not None:
            devtable_list.append(devtable)
        else:
            devtables = self.d.getVar('IMAGE_DEVICE_TABLES', True)
            if devtables is None:
                devtables = 'files/device_table-minimal.txt'
            for devtable in devtables.split():
                devtable_list.append("%s" % bb.utils.which(self.d.getVar('BBPATH', True), devtable))

        for devtable in devtable_list:
            self._exec_shell_cmd(["makedevs", "-r",
                                  self.image_rootfs, "-D", devtable])


class RpmRootfs(Rootfs):
    def __init__(self, d, manifest_dir, progress_reporter=None):
        super(RpmRootfs, self).__init__(d, progress_reporter)
        self.log_check_regex = '(unpacking of archive failed|Cannot find package'\
                               '|exit 1|ERROR: |Error: |Error |ERROR '\
                               '|Failed |Failed: |Failed$|Failed\(\d+\):)'
        self.manifest = RpmManifest(d, manifest_dir)

        self.pm = RpmPM(d,
                        d.getVar('IMAGE_ROOTFS', True),
                        self.d.getVar('TARGET_VENDOR', True)
                        )

        self.inc_rpm_image_gen = self.d.getVar('INC_RPM_IMAGE_GEN', True)
        if self.inc_rpm_image_gen != "1":
            bb.utils.remove(self.image_rootfs, True)
        else:
            self.pm.recovery_packaging_data()
        bb.utils.remove(self.d.getVar('MULTILIB_TEMP_ROOTFS', True), True)

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

    def _create(self):
        pkgs_to_install = self.manifest.parse_initial_manifest()
        rpm_pre_process_cmds = self.d.getVar('RPM_PREPROCESS_COMMANDS', True)
        rpm_post_process_cmds = self.d.getVar('RPM_POSTPROCESS_COMMANDS', True)

        # update PM index files
        self.pm.write_index()

        execute_pre_post_process(self.d, rpm_pre_process_cmds)

        if self.progress_reporter:
            self.progress_reporter.next_stage()

        self.pm.dump_all_available_pkgs()

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

        self._setup_dbg_rootfs(['/etc/rpm', '/var/lib/rpm', '/var/lib/smart'])

        execute_pre_post_process(self.d, rpm_post_process_cmds)

        self._log_check()

        if self.inc_rpm_image_gen == "1":
            self.pm.backup_packaging_data()

        self.pm.rpm_setup_smart_target_config()

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

    def _handle_intercept_failure(self, registered_pkgs):
        rpm_postinsts_dir = self.image_rootfs + self.d.expand('${sysconfdir}/rpm-postinsts/')
        bb.utils.mkdirhier(rpm_postinsts_dir)

        # Save the package postinstalls in /etc/rpm-postinsts
        for pkg in registered_pkgs.split():
            self.pm.save_rpmpostinst(pkg)

    def _cleanup(self):
        # during the execution of postprocess commands, rpm is called several
        # times to get the files installed, dependencies, etc. This creates the
        # __db.00* (Berkeley DB files that hold locks, rpm specific environment
        # settings, etc.), that should not get into the final rootfs
        self.pm.unlock_rpm_db()
        if os.path.isdir(self.pm.install_dir_path + "/tmp") and not os.listdir(self.pm.install_dir_path + "/tmp"):
           bb.utils.remove(self.pm.install_dir_path + "/tmp", True)
        if os.path.isdir(self.pm.install_dir_path) and not os.listdir(self.pm.install_dir_path):
           bb.utils.remove(self.pm.install_dir_path, True)

class DpkgOpkgRootfs(Rootfs):
    def __init__(self, d, progress_reporter=None):
        super(DpkgOpkgRootfs, self).__init__(d, progress_reporter)

    def _get_pkgs_postinsts(self, status_file):
        def _get_pkg_depends_list(pkg_depends):
            pkg_depends_list = []
            # filter version requirements like libc (>= 1.1)
            for dep in pkg_depends.split(', '):
                m_dep = re.match("^(.*) \(.*\)$", dep)
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
                m_pkg = re.match("^Package: (.*)", line)
                m_status = re.match("^Status:.*unpacked", line)
                m_depends = re.match("^Depends: (.*)", line)

                if m_pkg is not None:
                    if pkg_name and pkg_status_match:
                        pkgs[pkg_name] = _get_pkg_depends_list(pkg_depends)

                    pkg_name = m_pkg.group(1)
                    pkg_status_match = False
                    pkg_depends = ""
                elif m_status is not None:
                    pkg_status_match = True
                elif m_depends is not None:
                    pkg_depends = m_depends.group(1)

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
        if not self.d.getVar('PACKAGE_INSTALL', True).strip():
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
        num = 0
        for p in self._get_delayed_postinsts():
            bb.utils.mkdirhier(dst_postinst_dir)

            if os.path.exists(os.path.join(src_postinst_dir, p + ".postinst")):
                shutil.copy(os.path.join(src_postinst_dir, p + ".postinst"),
                            os.path.join(dst_postinst_dir, "%03d-%s" % (num, p)))

            num += 1

class DpkgRootfs(DpkgOpkgRootfs):
    def __init__(self, d, manifest_dir, progress_reporter=None):
        super(DpkgRootfs, self).__init__(d, progress_reporter)
        self.log_check_regex = '^E:'
        self.log_check_expected_regexes = \
        [
            "^E: Unmet dependencies."
        ]

        bb.utils.remove(self.image_rootfs, True)
        bb.utils.remove(self.d.getVar('MULTILIB_TEMP_ROOTFS', True), True)
        self.manifest = DpkgManifest(d, manifest_dir)
        self.pm = DpkgPM(d, d.getVar('IMAGE_ROOTFS', True),
                         d.getVar('PACKAGE_ARCHS', True),
                         d.getVar('DPKG_ARCH', True))


    def _create(self):
        pkgs_to_install = self.manifest.parse_initial_manifest()
        deb_pre_process_cmds = self.d.getVar('DEB_PREPROCESS_COMMANDS', True)
        deb_post_process_cmds = self.d.getVar('DEB_POSTPROCESS_COMMANDS', True)

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

    def _handle_intercept_failure(self, registered_pkgs):
        self.pm.mark_packages("unpacked", registered_pkgs.split())

    def _log_check(self):
        self._log_check_warn()
        self._log_check_error()

    def _cleanup(self):
        pass


class OpkgRootfs(DpkgOpkgRootfs):
    def __init__(self, d, manifest_dir, progress_reporter=None):
        super(OpkgRootfs, self).__init__(d, progress_reporter)
        self.log_check_regex = '(exit 1|Collected errors)'

        self.manifest = OpkgManifest(d, manifest_dir)
        self.opkg_conf = self.d.getVar("IPKGCONF_TARGET", True)
        self.pkg_archs = self.d.getVar("ALL_MULTILIB_PACKAGE_ARCHS", True)

        self.inc_opkg_image_gen = self.d.getVar('INC_IPK_IMAGE_GEN', True) or ""
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

        bb.utils.remove(self.d.getVar('MULTILIB_TEMP_ROOTFS', True), True)

    def _prelink_file(self, root_dir, filename):
        bb.note('prelink %s in %s' % (filename, root_dir))
        prelink_cfg = oe.path.join(root_dir,
                                   self.d.expand('${sysconfdir}/prelink.conf'))
        if not os.path.exists(prelink_cfg):
            shutil.copy(self.d.expand('${STAGING_DIR_NATIVE}${sysconfdir_native}/prelink.conf'),
                        prelink_cfg)

        cmd_prelink = self.d.expand('${STAGING_DIR_NATIVE}${sbindir_native}/prelink')
        self._exec_shell_cmd([cmd_prelink,
                              '--root',
                              root_dir,
                              '-amR',
                              '-N',
                              '-c',
                              self.d.expand('${sysconfdir}/prelink.conf')])

    '''
    Compare two files with the same key twice to see if they are equal.
    If they are not equal, it means they are duplicated and come from
    different packages.
    1st: Comapre them directly;
    2nd: While incremental image creation is enabled, one of the
         files could be probaly prelinked in the previous image
         creation and the file has been changed, so we need to
         prelink the other one and compare them.
    '''
    def _file_equal(self, key, f1, f2):

        # Both of them are not prelinked
        if filecmp.cmp(f1, f2):
            return True

        if self.image_rootfs not in f1:
            self._prelink_file(f1.replace(key, ''), f1)

        if self.image_rootfs not in f2:
            self._prelink_file(f2.replace(key, ''), f2)

        # Both of them are prelinked
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

        allow_replace = self.d.getVar("MULTILIBRE_ALLOW_REP", True)
        if allow_replace is None:
            allow_replace = ""

        allow_rep = re.compile(re.sub("\|$", "", allow_replace))
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
        ml_temp = self.d.getVar("MULTILIB_TEMP_ROOTFS", True)
        bb.utils.mkdirhier(ml_temp)

        dirs = [self.image_rootfs]

        for variant in self.d.getVar("MULTILIB_VARIANTS", True).split():
            ml_target_rootfs = os.path.join(ml_temp, variant)

            bb.utils.remove(ml_target_rootfs, True)

            ml_opkg_conf = os.path.join(ml_temp,
                                        variant + "-" + os.path.basename(self.opkg_conf))

            ml_pm = OpkgPM(self.d, ml_target_rootfs, ml_opkg_conf, self.pkg_archs)

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
                ((self.d.getVar('BAD_RECOMMENDATIONS', True) or '').strip(),
                 (self.d.getVar('NO_RECOMMENDATIONS', True) or '').strip(),
                 (self.d.getVar('PACKAGE_EXCLUDE', True) or '').strip())
        open(vars_list_file, 'w+').write(new_vars_list)

        if old_vars_list != new_vars_list:
            return True

        return False

    def _create(self):
        pkgs_to_install = self.manifest.parse_initial_manifest()
        opkg_pre_process_cmds = self.d.getVar('OPKG_PREPROCESS_COMMANDS', True)
        opkg_post_process_cmds = self.d.getVar('OPKG_POSTPROCESS_COMMANDS', True)

        # update PM index files, unless users provide their own feeds
        if (self.d.getVar('BUILD_IMAGES_FROM_FEEDS', True) or "") != "1":
            self.pm.write_index()

        execute_pre_post_process(self.d, opkg_pre_process_cmds)

        if self.progress_reporter:
            self.progress_reporter.next_stage()
            # Steps are a bit different in order, skip next
            self.progress_reporter.next_stage()

        self.pm.update()

        self.pm.handle_bad_recommendations()

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

        self._setup_dbg_rootfs(['/etc', '/var/lib/opkg', '/usr/lib/ssl'])

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
                                   self.d.getVar('OPKGLIBDIR', True).strip('/'),
                                   "opkg", "status")
        return self._get_delayed_postinsts_common(status_file)

    def _save_postinsts(self):
        dst_postinst_dir = self.d.expand("${IMAGE_ROOTFS}${sysconfdir}/ipk-postinsts")
        src_postinst_dir = self.d.expand("${IMAGE_ROOTFS}${OPKGLIBDIR}/opkg/info")
        return self._save_postinsts_common(dst_postinst_dir, src_postinst_dir)

    def _handle_intercept_failure(self, registered_pkgs):
        self.pm.mark_packages("unpacked", registered_pkgs.split())

    def _log_check(self):
        self._log_check_warn()
        self._log_check_error()

    def _cleanup(self):
        self.pm.remove_lists()

def get_class_for_type(imgtype):
    return {"rpm": RpmRootfs,
            "ipk": OpkgRootfs,
            "deb": DpkgRootfs}[imgtype]

def variable_depends(d, manifest_dir=None):
    img_type = d.getVar('IMAGE_PKGTYPE', True)
    cls = get_class_for_type(img_type)
    return cls._depends_list()

def create_rootfs(d, manifest_dir=None, progress_reporter=None):
    env_bkp = os.environ.copy()

    img_type = d.getVar('IMAGE_PKGTYPE', True)
    if img_type == "rpm":
        RpmRootfs(d, manifest_dir, progress_reporter).create()
    elif img_type == "ipk":
        OpkgRootfs(d, manifest_dir, progress_reporter).create()
    elif img_type == "deb":
        DpkgRootfs(d, manifest_dir, progress_reporter).create()

    os.environ.clear()
    os.environ.update(env_bkp)


def image_list_installed_packages(d, rootfs_dir=None):
    if not rootfs_dir:
        rootfs_dir = d.getVar('IMAGE_ROOTFS', True)

    img_type = d.getVar('IMAGE_PKGTYPE', True)
    if img_type == "rpm":
        return RpmPkgsList(d, rootfs_dir).list_pkgs()
    elif img_type == "ipk":
        return OpkgPkgsList(d, rootfs_dir, d.getVar("IPKGCONF_TARGET", True)).list_pkgs()
    elif img_type == "deb":
        return DpkgPkgsList(d, rootfs_dir).list_pkgs()

if __name__ == "__main__":
    """
    We should be able to run this as a standalone script, from outside bitbake
    environment.
    """
    """
    TBD
    """
