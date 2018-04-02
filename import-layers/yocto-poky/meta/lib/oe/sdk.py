from abc import ABCMeta, abstractmethod
from oe.utils import execute_pre_post_process
from oe.manifest import *
from oe.package_manager import *
import os
import shutil
import glob
import traceback

def generate_locale_archive(d, rootfs):
    # Pretty sure we don't need this for SDK archive generation but
    # keeping it to be safe...
    target_arch = d.getVar('SDK_ARCH')
    locale_arch_options = { \
        "arm": ["--uint32-align=4", "--little-endian"],
        "armeb": ["--uint32-align=4", "--big-endian"],
        "aarch64": ["--uint32-align=4", "--little-endian"],
        "aarch64_be": ["--uint32-align=4", "--big-endian"],
        "sh4": ["--uint32-align=4", "--big-endian"],
        "powerpc": ["--uint32-align=4", "--big-endian"],
        "powerpc64": ["--uint32-align=4", "--big-endian"],
        "mips": ["--uint32-align=4", "--big-endian"],
        "mipsisa32r6": ["--uint32-align=4", "--big-endian"],
        "mips64": ["--uint32-align=4", "--big-endian"],
        "mipsisa64r6": ["--uint32-align=4", "--big-endian"],
        "mipsel": ["--uint32-align=4", "--little-endian"],
        "mipsisa32r6el": ["--uint32-align=4", "--little-endian"],
        "mips64el": ["--uint32-align=4", "--little-endian"],
        "mipsisa64r6el": ["--uint32-align=4", "--little-endian"],
        "i586": ["--uint32-align=4", "--little-endian"],
        "i686": ["--uint32-align=4", "--little-endian"],
        "x86_64": ["--uint32-align=4", "--little-endian"]
    }
    if target_arch in locale_arch_options:
        arch_options = locale_arch_options[target_arch]
    else:
        bb.error("locale_arch_options not found for target_arch=" + target_arch)
        bb.fatal("unknown arch:" + target_arch + " for locale_arch_options")

    localedir = oe.path.join(rootfs, d.getVar("libdir_nativesdk"), "locale")
    # Need to set this so cross-localedef knows where the archive is
    env = dict(os.environ)
    env["LOCALEARCHIVE"] = oe.path.join(localedir, "locale-archive")

    for name in os.listdir(localedir):
        path = os.path.join(localedir, name)
        if os.path.isdir(path):
            try:
                cmd = ["cross-localedef", "--verbose"]
                cmd += arch_options
                cmd += ["--add-to-archive", path]
                subprocess.check_output(cmd, env=env, stderr=subprocess.STDOUT)
            except Exception as e:
                bb.fatal("Cannot create locale archive: %s" % e.output)

class Sdk(object, metaclass=ABCMeta):
    def __init__(self, d, manifest_dir):
        self.d = d
        self.sdk_output = self.d.getVar('SDK_OUTPUT')
        self.sdk_native_path = self.d.getVar('SDKPATHNATIVE').strip('/')
        self.target_path = self.d.getVar('SDKTARGETSYSROOT').strip('/')
        self.sysconfdir = self.d.getVar('sysconfdir').strip('/')

        self.sdk_target_sysroot = os.path.join(self.sdk_output, self.target_path)
        self.sdk_host_sysroot = self.sdk_output

        if manifest_dir is None:
            self.manifest_dir = self.d.getVar("SDK_DIR")
        else:
            self.manifest_dir = manifest_dir

        self.remove(self.sdk_output, True)

        self.install_order = Manifest.INSTALL_ORDER

    @abstractmethod
    def _populate(self):
        pass

    def populate(self):
        self.mkdirhier(self.sdk_output)

        # call backend dependent implementation
        self._populate()

        # Don't ship any libGL in the SDK
        self.remove(os.path.join(self.sdk_output, self.sdk_native_path,
                         self.d.getVar('libdir_nativesdk').strip('/'),
                         "libGL*"))

        # Fix or remove broken .la files
        self.remove(os.path.join(self.sdk_output, self.sdk_native_path,
                         self.d.getVar('libdir_nativesdk').strip('/'),
                         "*.la"))

        # Link the ld.so.cache file into the hosts filesystem
        link_name = os.path.join(self.sdk_output, self.sdk_native_path,
                                 self.sysconfdir, "ld.so.cache")
        self.mkdirhier(os.path.dirname(link_name))
        os.symlink("/etc/ld.so.cache", link_name)

        execute_pre_post_process(self.d, self.d.getVar('SDK_POSTPROCESS_COMMAND'))

    def movefile(self, sourcefile, destdir):
        try:
            # FIXME: this check of movefile's return code to None should be
            # fixed within the function to use only exceptions to signal when
            # something goes wrong
            if (bb.utils.movefile(sourcefile, destdir) == None):
                raise OSError("moving %s to %s failed"
                        %(sourcefile, destdir))
        #FIXME: using umbrella exc catching because bb.utils method raises it
        except Exception as e:
            bb.debug(1, "printing the stack trace\n %s" %traceback.format_exc())
            bb.error("unable to place %s in final SDK location" % sourcefile)

    def mkdirhier(self, dirpath):
        try:
            bb.utils.mkdirhier(dirpath)
        except OSError as e:
            bb.debug(1, "printing the stack trace\n %s" %traceback.format_exc())
            bb.fatal("cannot make dir for SDK: %s" % dirpath)

    def remove(self, path, recurse=False):
        try:
            bb.utils.remove(path, recurse)
        #FIXME: using umbrella exc catching because bb.utils method raises it
        except Exception as e:
            bb.debug(1, "printing the stack trace\n %s" %traceback.format_exc())
            bb.warn("cannot remove SDK dir: %s" % path)

    def install_locales(self, pm):
        # This is only relevant for glibc
        if self.d.getVar("TCLIBC") != "glibc":
            return

        linguas = self.d.getVar("SDKIMAGE_LINGUAS")
        if linguas:
            import fnmatch
            # Install the binary locales
            if linguas == "all":
                pm.install_glob("nativesdk-glibc-binary-localedata-*.utf-8", sdk=True)
            else:
                for lang in linguas.split():
                    pm.install("nativesdk-glibc-binary-localedata-%s.utf-8" % lang)
            # Generate a locale archive of them
            generate_locale_archive(self.d, oe.path.join(self.sdk_host_sysroot, self.sdk_native_path))
            # And now delete the binary locales
            pkgs = fnmatch.filter(pm.list_installed(), "nativesdk-glibc-binary-localedata-*.utf-8")
            pm.remove(pkgs)
        else:
            # No linguas so do nothing
            pass


class RpmSdk(Sdk):
    def __init__(self, d, manifest_dir=None, rpm_workdir="oe-sdk-repo"):
        super(RpmSdk, self).__init__(d, manifest_dir)

        self.target_manifest = RpmManifest(d, self.manifest_dir,
                                           Manifest.MANIFEST_TYPE_SDK_TARGET)
        self.host_manifest = RpmManifest(d, self.manifest_dir,
                                         Manifest.MANIFEST_TYPE_SDK_HOST)

        target_providename = ['/bin/sh',
                              '/bin/bash',
                              '/usr/bin/env',
                              '/usr/bin/perl',
                              'pkgconfig'
                              ]

        rpm_repo_workdir = "oe-sdk-repo"
        if "sdk_ext" in d.getVar("BB_RUNTASK"):
            rpm_repo_workdir = "oe-sdk-ext-repo"


        self.target_pm = RpmPM(d,
                               self.sdk_target_sysroot,
                               self.d.getVar('TARGET_VENDOR'),
                               'target',
                               target_providename,
                               rpm_repo_workdir=rpm_repo_workdir
                               )

        sdk_providename = ['/bin/sh',
                           '/bin/bash',
                           '/usr/bin/env',
                           '/usr/bin/perl',
                           'pkgconfig',
                           'libGL.so()(64bit)',
                           'libGL.so'
                           ]

        self.host_pm = RpmPM(d,
                             self.sdk_host_sysroot,
                             self.d.getVar('SDK_VENDOR'),
                             'host',
                             sdk_providename,
                             "SDK_PACKAGE_ARCHS",
                             "SDK_OS",
                             rpm_repo_workdir=rpm_repo_workdir
                             )

    def _populate_sysroot(self, pm, manifest):
        pkgs_to_install = manifest.parse_initial_manifest()

        pm.create_configs()
        pm.write_index()
        pm.update()

        pkgs = []
        pkgs_attempt = []
        for pkg_type in pkgs_to_install:
            if pkg_type == Manifest.PKG_TYPE_ATTEMPT_ONLY:
                pkgs_attempt += pkgs_to_install[pkg_type]
            else:
                pkgs += pkgs_to_install[pkg_type]

        pm.install(pkgs)

        pm.install(pkgs_attempt, True)

    def _populate(self):
        execute_pre_post_process(self.d, self.d.getVar("POPULATE_SDK_PRE_TARGET_COMMAND"))

        bb.note("Installing TARGET packages")
        self._populate_sysroot(self.target_pm, self.target_manifest)

        self.target_pm.install_complementary(self.d.getVar('SDKIMAGE_INSTALL_COMPLEMENTARY'))

        execute_pre_post_process(self.d, self.d.getVar("POPULATE_SDK_POST_TARGET_COMMAND"))

        if not bb.utils.contains("SDKIMAGE_FEATURES", "package-management", True, False, self.d):
            self.target_pm.remove_packaging_data()

        bb.note("Installing NATIVESDK packages")
        self._populate_sysroot(self.host_pm, self.host_manifest)
        self.install_locales(self.host_pm)

        execute_pre_post_process(self.d, self.d.getVar("POPULATE_SDK_POST_HOST_COMMAND"))

        if not bb.utils.contains("SDKIMAGE_FEATURES", "package-management", True, False, self.d):
            self.host_pm.remove_packaging_data()

        # Move host RPM library data
        native_rpm_state_dir = os.path.join(self.sdk_output,
                                            self.sdk_native_path,
                                            self.d.getVar('localstatedir_nativesdk').strip('/'),
                                            "lib",
                                            "rpm"
                                            )
        self.mkdirhier(native_rpm_state_dir)
        for f in glob.glob(os.path.join(self.sdk_output,
                                        "var",
                                        "lib",
                                        "rpm",
                                        "*")):
            self.movefile(f, native_rpm_state_dir)

        self.remove(os.path.join(self.sdk_output, "var"), True)

        # Move host sysconfig data
        native_sysconf_dir = os.path.join(self.sdk_output,
                                          self.sdk_native_path,
                                          self.d.getVar('sysconfdir',
                                                        True).strip('/'),
                                          )
        self.mkdirhier(native_sysconf_dir)
        for f in glob.glob(os.path.join(self.sdk_output, "etc", "rpm*")):
            self.movefile(f, native_sysconf_dir)
        for f in glob.glob(os.path.join(self.sdk_output, "etc", "dnf", "*")):
            self.movefile(f, native_sysconf_dir)
        self.remove(os.path.join(self.sdk_output, "etc"), True)


class OpkgSdk(Sdk):
    def __init__(self, d, manifest_dir=None):
        super(OpkgSdk, self).__init__(d, manifest_dir)

        self.target_conf = self.d.getVar("IPKGCONF_TARGET")
        self.host_conf = self.d.getVar("IPKGCONF_SDK")

        self.target_manifest = OpkgManifest(d, self.manifest_dir,
                                            Manifest.MANIFEST_TYPE_SDK_TARGET)
        self.host_manifest = OpkgManifest(d, self.manifest_dir,
                                          Manifest.MANIFEST_TYPE_SDK_HOST)

        self.target_pm = OpkgPM(d, self.sdk_target_sysroot, self.target_conf,
                                self.d.getVar("ALL_MULTILIB_PACKAGE_ARCHS"))

        self.host_pm = OpkgPM(d, self.sdk_host_sysroot, self.host_conf,
                              self.d.getVar("SDK_PACKAGE_ARCHS"))

    def _populate_sysroot(self, pm, manifest):
        pkgs_to_install = manifest.parse_initial_manifest()

        if (self.d.getVar('BUILD_IMAGES_FROM_FEEDS') or "") != "1":
            pm.write_index()

        pm.update()

        for pkg_type in self.install_order:
            if pkg_type in pkgs_to_install:
                pm.install(pkgs_to_install[pkg_type],
                           [False, True][pkg_type == Manifest.PKG_TYPE_ATTEMPT_ONLY])

    def _populate(self):
        execute_pre_post_process(self.d, self.d.getVar("POPULATE_SDK_PRE_TARGET_COMMAND"))

        bb.note("Installing TARGET packages")
        self._populate_sysroot(self.target_pm, self.target_manifest)

        self.target_pm.install_complementary(self.d.getVar('SDKIMAGE_INSTALL_COMPLEMENTARY'))

        execute_pre_post_process(self.d, self.d.getVar("POPULATE_SDK_POST_TARGET_COMMAND"))

        if not bb.utils.contains("SDKIMAGE_FEATURES", "package-management", True, False, self.d):
            self.target_pm.remove_packaging_data()

        bb.note("Installing NATIVESDK packages")
        self._populate_sysroot(self.host_pm, self.host_manifest)
        self.install_locales(self.host_pm)

        execute_pre_post_process(self.d, self.d.getVar("POPULATE_SDK_POST_HOST_COMMAND"))

        if not bb.utils.contains("SDKIMAGE_FEATURES", "package-management", True, False, self.d):
            self.host_pm.remove_packaging_data()

        target_sysconfdir = os.path.join(self.sdk_target_sysroot, self.sysconfdir)
        host_sysconfdir = os.path.join(self.sdk_host_sysroot, self.sysconfdir)

        self.mkdirhier(target_sysconfdir)
        shutil.copy(self.target_conf, target_sysconfdir)
        os.chmod(os.path.join(target_sysconfdir,
                              os.path.basename(self.target_conf)), 0o644)

        self.mkdirhier(host_sysconfdir)
        shutil.copy(self.host_conf, host_sysconfdir)
        os.chmod(os.path.join(host_sysconfdir,
                              os.path.basename(self.host_conf)), 0o644)

        native_opkg_state_dir = os.path.join(self.sdk_output, self.sdk_native_path,
                                             self.d.getVar('localstatedir_nativesdk').strip('/'),
                                             "lib", "opkg")
        self.mkdirhier(native_opkg_state_dir)
        for f in glob.glob(os.path.join(self.sdk_output, "var", "lib", "opkg", "*")):
            self.movefile(f, native_opkg_state_dir)

        self.remove(os.path.join(self.sdk_output, "var"), True)


class DpkgSdk(Sdk):
    def __init__(self, d, manifest_dir=None):
        super(DpkgSdk, self).__init__(d, manifest_dir)

        self.target_conf_dir = os.path.join(self.d.getVar("APTCONF_TARGET"), "apt")
        self.host_conf_dir = os.path.join(self.d.getVar("APTCONF_TARGET"), "apt-sdk")

        self.target_manifest = DpkgManifest(d, self.manifest_dir,
                                            Manifest.MANIFEST_TYPE_SDK_TARGET)
        self.host_manifest = DpkgManifest(d, self.manifest_dir,
                                          Manifest.MANIFEST_TYPE_SDK_HOST)

        self.target_pm = DpkgPM(d, self.sdk_target_sysroot,
                                self.d.getVar("PACKAGE_ARCHS"),
                                self.d.getVar("DPKG_ARCH"),
                                self.target_conf_dir)

        self.host_pm = DpkgPM(d, self.sdk_host_sysroot,
                              self.d.getVar("SDK_PACKAGE_ARCHS"),
                              self.d.getVar("DEB_SDK_ARCH"),
                              self.host_conf_dir)

    def _copy_apt_dir_to(self, dst_dir):
        staging_etcdir_native = self.d.getVar("STAGING_ETCDIR_NATIVE")

        self.remove(dst_dir, True)

        shutil.copytree(os.path.join(staging_etcdir_native, "apt"), dst_dir)

    def _populate_sysroot(self, pm, manifest):
        pkgs_to_install = manifest.parse_initial_manifest()

        pm.write_index()
        pm.update()

        for pkg_type in self.install_order:
            if pkg_type in pkgs_to_install:
                pm.install(pkgs_to_install[pkg_type],
                           [False, True][pkg_type == Manifest.PKG_TYPE_ATTEMPT_ONLY])

    def _populate(self):
        execute_pre_post_process(self.d, self.d.getVar("POPULATE_SDK_PRE_TARGET_COMMAND"))

        bb.note("Installing TARGET packages")
        self._populate_sysroot(self.target_pm, self.target_manifest)

        self.target_pm.install_complementary(self.d.getVar('SDKIMAGE_INSTALL_COMPLEMENTARY'))

        execute_pre_post_process(self.d, self.d.getVar("POPULATE_SDK_POST_TARGET_COMMAND"))

        self._copy_apt_dir_to(os.path.join(self.sdk_target_sysroot, "etc", "apt"))

        if not bb.utils.contains("SDKIMAGE_FEATURES", "package-management", True, False, self.d):
            self.target_pm.remove_packaging_data()

        bb.note("Installing NATIVESDK packages")
        self._populate_sysroot(self.host_pm, self.host_manifest)
        self.install_locales(self.host_pm)

        execute_pre_post_process(self.d, self.d.getVar("POPULATE_SDK_POST_HOST_COMMAND"))

        self._copy_apt_dir_to(os.path.join(self.sdk_output, self.sdk_native_path,
                                           "etc", "apt"))

        if not bb.utils.contains("SDKIMAGE_FEATURES", "package-management", True, False, self.d):
            self.host_pm.remove_packaging_data()

        native_dpkg_state_dir = os.path.join(self.sdk_output, self.sdk_native_path,
                                             "var", "lib", "dpkg")
        self.mkdirhier(native_dpkg_state_dir)
        for f in glob.glob(os.path.join(self.sdk_output, "var", "lib", "dpkg", "*")):
            self.movefile(f, native_dpkg_state_dir)
        self.remove(os.path.join(self.sdk_output, "var"), True)



def sdk_list_installed_packages(d, target, rootfs_dir=None):
    if rootfs_dir is None:
        sdk_output = d.getVar('SDK_OUTPUT')
        target_path = d.getVar('SDKTARGETSYSROOT').strip('/')

        rootfs_dir = [sdk_output, os.path.join(sdk_output, target_path)][target is True]

    img_type = d.getVar('IMAGE_PKGTYPE')
    if img_type == "rpm":
        arch_var = ["SDK_PACKAGE_ARCHS", None][target is True]
        os_var = ["SDK_OS", None][target is True]
        return RpmPkgsList(d, rootfs_dir).list_pkgs()
    elif img_type == "ipk":
        conf_file_var = ["IPKGCONF_SDK", "IPKGCONF_TARGET"][target is True]
        return OpkgPkgsList(d, rootfs_dir, d.getVar(conf_file_var)).list_pkgs()
    elif img_type == "deb":
        return DpkgPkgsList(d, rootfs_dir).list_pkgs()

def populate_sdk(d, manifest_dir=None):
    env_bkp = os.environ.copy()

    img_type = d.getVar('IMAGE_PKGTYPE')
    if img_type == "rpm":
        RpmSdk(d, manifest_dir).populate()
    elif img_type == "ipk":
        OpkgSdk(d, manifest_dir).populate()
    elif img_type == "deb":
        DpkgSdk(d, manifest_dir).populate()

    os.environ.clear()
    os.environ.update(env_bkp)

def get_extra_sdkinfo(sstate_dir):
    """
    This function is going to be used for generating the target and host manifest files packages of eSDK.
    """
    import math
    
    extra_info = {}
    extra_info['tasksizes'] = {}
    extra_info['filesizes'] = {}
    for root, _, files in os.walk(sstate_dir):
        for fn in files:
            if fn.endswith('.tgz'):
                fsize = int(math.ceil(float(os.path.getsize(os.path.join(root, fn))) / 1024))
                task = fn.rsplit(':',1)[1].split('_',1)[1].split(',')[0]
                origtotal = extra_info['tasksizes'].get(task, 0)
                extra_info['tasksizes'][task] = origtotal + fsize
                extra_info['filesizes'][fn] = fsize
    return extra_info

if __name__ == "__main__":
    pass
