#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: GPL-2.0-only
#
from abc import ABCMeta, abstractmethod
from oe.utils import execute_pre_post_process
from oe.package_manager import *
from oe.manifest import *
import oe.path
import shutil
import os
import subprocess
import re

class Rootfs(object, metaclass=ABCMeta):
    """
    This is an abstract class. Do not instantiate this directly.
    """

    def __init__(self, d, progress_reporter=None, logcatcher=None):
        self.d = d
        self.pm = None
        self.image_rootfs = self.d.getVar('IMAGE_ROOTFS')
        self.deploydir = self.d.getVar('IMGDEPLOYDIR')
        self.progress_reporter = progress_reporter
        self.logcatcher = logcatcher

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
        # Insert custom log_check excludes
        excludes += [x for x in (self.d.getVar("IMAGE_LOG_CHECK_EXCLUDES") or "").split(" ") if x]
        excludes = [re.compile(x) for x in excludes]
        r = re.compile(match)
        log_path = self.d.expand("${T}/log.do_rootfs")
        messages = []
        with open(log_path, 'r') as log:
            for line in log:
                if self.logcatcher and self.logcatcher.contains(line.rstrip()):
                    continue
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
                (self.d.getVar('PN'), msg, ''.join(messages))
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
            self.pm.insert_feeds_uris(self.d.getVar('PACKAGE_FEED_URIS') or "",
                self.d.getVar('PACKAGE_FEED_BASE_PATHS') or "",
                self.d.getVar('PACKAGE_FEED_ARCHS'))


    """
    The _cleanup() method should be used to clean-up stuff that we don't really
    want to end up on target. For example, in the case of RPM, the DB locks.
    The method is called, once, at the end of create() method.
    """
    @abstractmethod
    def _cleanup(self):
        pass

    def _setup_dbg_rootfs(self, package_paths):
        gen_debugfs = self.d.getVar('IMAGE_GEN_DEBUGFS') or '0'
        if gen_debugfs != '1':
           return

        bb.note("  Renaming the original rootfs...")
        try:
            shutil.rmtree(self.image_rootfs + '-orig')
        except:
            pass
        bb.utils.rename(self.image_rootfs, self.image_rootfs + '-orig')

        bb.note("  Creating debug rootfs...")
        bb.utils.mkdirhier(self.image_rootfs)

        bb.note("  Copying back package database...")
        for path in package_paths:
            bb.utils.mkdirhier(self.image_rootfs + os.path.dirname(path))
            if os.path.isdir(self.image_rootfs + '-orig' + path):
                shutil.copytree(self.image_rootfs + '-orig' + path, self.image_rootfs + path, symlinks=True)
            elif os.path.isfile(self.image_rootfs + '-orig' + path):
                shutil.copyfile(self.image_rootfs + '-orig' + path, self.image_rootfs + path)

        # Copy files located in /usr/lib/debug or /usr/src/debug
        for dir in ["/usr/lib/debug", "/usr/src/debug"]:
            src = self.image_rootfs + '-orig' + dir
            if os.path.exists(src):
                dst = self.image_rootfs + dir
                bb.utils.mkdirhier(os.path.dirname(dst))
                shutil.copytree(src, dst)

        # Copy files with suffix '.debug' or located in '.debug' dir.
        for root, dirs, files in os.walk(self.image_rootfs + '-orig'):
            relative_dir = root[len(self.image_rootfs + '-orig'):]
            for f in files:
                if f.endswith('.debug') or '/.debug' in relative_dir:
                    bb.utils.mkdirhier(self.image_rootfs + relative_dir)
                    shutil.copy(os.path.join(root, f),
                                self.image_rootfs + relative_dir)

        bb.note("  Install complementary '*-dbg' packages...")
        self.pm.install_complementary('*-dbg')

        if self.d.getVar('PACKAGE_DEBUG_SPLIT_STYLE') == 'debug-with-srcpkg':
            bb.note("  Install complementary '*-src' packages...")
            self.pm.install_complementary('*-src')

        """
        Install additional debug packages. Possibility to install additional packages,
        which are not automatically installed as complementary package of
        standard one, e.g. debug package of static libraries.
        """
        extra_debug_pkgs = self.d.getVar('IMAGE_INSTALL_DEBUGFS')
        if extra_debug_pkgs:
            bb.note("  Install extra debug packages...")
            self.pm.install(extra_debug_pkgs.split(), True)

        bb.note("  Removing package database...")
        for path in package_paths:
            if os.path.isdir(self.image_rootfs + path):
                shutil.rmtree(self.image_rootfs + path)
            elif os.path.isfile(self.image_rootfs + path):
                os.remove(self.image_rootfs + path)

        bb.note("  Rename debug rootfs...")
        try:
            shutil.rmtree(self.image_rootfs + '-dbg')
        except:
            pass
        bb.utils.rename(self.image_rootfs, self.image_rootfs + '-dbg')

        bb.note("  Restoring original rootfs...")
        bb.utils.rename(self.image_rootfs + '-orig', self.image_rootfs)

    def _exec_shell_cmd(self, cmd):
        try:
            subprocess.check_output(cmd, stderr=subprocess.STDOUT)
        except subprocess.CalledProcessError as e:
            return("Command '%s' returned %d:\n%s" % (e.cmd, e.returncode, e.output))

        return None

    def create(self):
        bb.note("###### Generate rootfs #######")
        pre_process_cmds = self.d.getVar("ROOTFS_PREPROCESS_COMMAND")
        post_process_cmds = self.d.getVar("ROOTFS_POSTPROCESS_COMMAND")
        rootfs_post_install_cmds = self.d.getVar('ROOTFS_POSTINSTALL_COMMAND')

        def make_last(command, commands):
            commands = commands.split()
            if command in commands:
                commands.remove(command)
                commands.append(command)
            return "".join(commands)

        # We want this to run as late as possible, in particular after
        # systemd_sysusers_create and set_user_group. Using :append is not enough
        make_last("tidy_shadowutils_files", post_process_cmds)
        make_last("rootfs_reproducible", post_process_cmds)

        execute_pre_post_process(self.d, pre_process_cmds)

        if self.progress_reporter:
            self.progress_reporter.next_stage()

        # call the package manager dependent create method
        self._create()

        sysconfdir = self.image_rootfs + self.d.getVar('sysconfdir')
        bb.utils.mkdirhier(sysconfdir)
        with open(sysconfdir + "/version", "w+") as ver:
            ver.write(self.d.getVar('BUILDNAME') + "\n")

        execute_pre_post_process(self.d, rootfs_post_install_cmds)

        self.pm.run_intercepts()

        execute_pre_post_process(self.d, post_process_cmds)

        if self.progress_reporter:
            self.progress_reporter.next_stage()

        if bb.utils.contains("IMAGE_FEATURES", "read-only-rootfs",
                         True, False, self.d) and \
           not bb.utils.contains("IMAGE_FEATURES",
                         "read-only-rootfs-delayed-postinsts",
                         True, False, self.d):
            delayed_postinsts = self._get_delayed_postinsts()
            if delayed_postinsts is not None:
                bb.fatal("The following packages could not be configured "
                         "offline and rootfs is read-only: %s" %
                         delayed_postinsts)

        if self.d.getVar('USE_DEVFS') != "1":
            self._create_devfs()

        self._uninstall_unneeded()

        if self.progress_reporter:
            self.progress_reporter.next_stage()

        self._insert_feed_uris()

        self._run_ldconfig()

        if self.d.getVar('USE_DEPMOD') != "0":
            self._generate_kernel_module_deps()

        self._cleanup()
        self._log_check()

        if self.progress_reporter:
            self.progress_reporter.next_stage()


    def _uninstall_unneeded(self):
        # Remove the run-postinsts package if no delayed postinsts are found
        delayed_postinsts = self._get_delayed_postinsts()
        if delayed_postinsts is None:
            if os.path.exists(self.d.expand("${IMAGE_ROOTFS}${sysconfdir}/init.d/run-postinsts")) or os.path.exists(self.d.expand("${IMAGE_ROOTFS}${systemd_system_unitdir}/run-postinsts.service")):
                self.pm.remove(["run-postinsts"])

        image_rorfs = bb.utils.contains("IMAGE_FEATURES", "read-only-rootfs",
                                        True, False, self.d)
        image_rorfs_force = self.d.getVar('FORCE_RO_REMOVE')

        if image_rorfs or image_rorfs_force == "1":
            # Remove components that we don't need if it's a read-only rootfs
            unneeded_pkgs = self.d.getVar("ROOTFS_RO_UNNEEDED").split()
            pkgs_installed = image_list_installed_packages(self.d)
            # Make sure update-alternatives is removed last. This is
            # because its database has to available while uninstalling
            # other packages, allowing alternative symlinks of packages
            # to be uninstalled or to be managed correctly otherwise.
            provider = self.d.getVar("VIRTUAL-RUNTIME_update-alternatives")
            pkgs_to_remove = sorted([pkg for pkg in pkgs_installed if pkg in unneeded_pkgs], key=lambda x: x == provider)

            # update-alternatives provider is removed in its own remove()
            # call because all package managers do not guarantee the packages
            # are removed in the order they given in the list (which is
            # passed to the command line). The sorting done earlier is
            # utilized to implement the 2-stage removal.
            if len(pkgs_to_remove) > 1:
                self.pm.remove(pkgs_to_remove[:-1], False)
            if len(pkgs_to_remove) > 0:
                self.pm.remove([pkgs_to_remove[-1]], False)

        if delayed_postinsts:
            self._save_postinsts()
            if image_rorfs:
                bb.warn("There are post install scripts "
                        "in a read-only rootfs")

        post_uninstall_cmds = self.d.getVar("ROOTFS_POSTUNINSTALL_COMMAND")
        execute_pre_post_process(self.d, post_uninstall_cmds)

        runtime_pkgmanage = bb.utils.contains("IMAGE_FEATURES", "package-management",
                                              True, False, self.d)
        if not runtime_pkgmanage:
            # Remove the package manager data files
            self.pm.remove_packaging_data()

    def _run_ldconfig(self):
        if self.d.getVar('LDCONFIGDEPEND'):
            bb.note("Executing: ldconfig -r " + self.image_rootfs + " -c new -v -X")
            self._exec_shell_cmd(['ldconfig', '-r', self.image_rootfs, '-c',
                                  'new', '-v', '-X'])

        image_rorfs = bb.utils.contains("IMAGE_FEATURES", "read-only-rootfs",
                                        True, False, self.d)
        ldconfig_in_features = bb.utils.contains("DISTRO_FEATURES", "ldconfig",
                                                 True, False, self.d)
        if image_rorfs or not ldconfig_in_features:
            ldconfig_cache_dir = os.path.join(self.image_rootfs, "var/cache/ldconfig")
            if os.path.exists(ldconfig_cache_dir):
                bb.note("Removing ldconfig auxiliary cache...")
                shutil.rmtree(ldconfig_cache_dir)

    def _check_for_kernel_modules(self, modules_dir):
        for root, dirs, files in os.walk(modules_dir, topdown=True):
            for name in files:
                found_ko = name.endswith((".ko", ".ko.gz", ".ko.xz", ".ko.zst"))
                if found_ko:
                    return found_ko
        return False

    def _generate_kernel_module_deps(self):
        modules_dir = os.path.join(self.image_rootfs, 'lib', 'modules')
        # if we don't have any modules don't bother to do the depmod
        if not self._check_for_kernel_modules(modules_dir):
            bb.note("No Kernel Modules found, not running depmod")
            return

        pkgdatadir = self.d.getVar('PKGDATA_DIR')

        # PKGDATA_DIR can include multiple kernels so we run depmod for each
        # one of them.
        for direntry in os.listdir(pkgdatadir):
            match = re.match('(.*)-depmod', direntry)
            if not match:
                continue
            kernel_package_name = match.group(1)

            kernel_abi_ver_file = oe.path.join(pkgdatadir, direntry, kernel_package_name + '-abiversion')
            if not os.path.exists(kernel_abi_ver_file):
                bb.fatal("No kernel-abiversion file found (%s), cannot run depmod, aborting" % kernel_abi_ver_file)

            with open(kernel_abi_ver_file) as f:
                kernel_ver = f.read().strip(' \n')

            versioned_modules_dir = os.path.join(self.image_rootfs, modules_dir, kernel_ver)

            bb.utils.mkdirhier(versioned_modules_dir)

            bb.note("Running depmodwrapper for %s ..." % versioned_modules_dir)
            if self._exec_shell_cmd(['depmodwrapper', '-a', '-b', self.image_rootfs, kernel_ver, kernel_package_name]):
                bb.fatal("Kernel modules dependency generation failed")

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
        devtable = self.d.getVar('IMAGE_DEVICE_TABLE')
        if devtable is not None:
            devtable_list.append(devtable)
        else:
            devtables = self.d.getVar('IMAGE_DEVICE_TABLES')
            if devtables is None:
                devtables = 'files/device_table-minimal.txt'
            for devtable in devtables.split():
                devtable_list.append("%s" % bb.utils.which(self.d.getVar('BBPATH'), devtable))

        for devtable in devtable_list:
            self._exec_shell_cmd(["makedevs", "-r",
                                  self.image_rootfs, "-D", devtable])


def get_class_for_type(imgtype):
    import importlib
    mod = importlib.import_module('oe.package_manager.' + imgtype + '.rootfs')
    return mod.PkgRootfs

def variable_depends(d, manifest_dir=None):
    img_type = d.getVar('IMAGE_PKGTYPE')
    cls = get_class_for_type(img_type)
    return cls._depends_list()

def create_rootfs(d, manifest_dir=None, progress_reporter=None, logcatcher=None):
    env_bkp = os.environ.copy()

    img_type = d.getVar('IMAGE_PKGTYPE')

    cls = get_class_for_type(img_type)
    cls(d, manifest_dir, progress_reporter, logcatcher).create()
    os.environ.clear()
    os.environ.update(env_bkp)


def image_list_installed_packages(d, rootfs_dir=None):
    # Theres no rootfs for baremetal images
    if bb.data.inherits_class('baremetal-image', d):
        return ""

    if not rootfs_dir:
        rootfs_dir = d.getVar('IMAGE_ROOTFS')

    img_type = d.getVar('IMAGE_PKGTYPE')

    import importlib
    cls = importlib.import_module('oe.package_manager.' + img_type)
    return cls.PMPkgsList(d, rootfs_dir).list_pkgs()

if __name__ == "__main__":
    """
    We should be able to run this as a standalone script, from outside bitbake
    environment.
    """
    """
    TBD
    """
