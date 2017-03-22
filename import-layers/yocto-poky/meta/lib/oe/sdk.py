from abc import ABCMeta, abstractmethod
from oe.utils import execute_pre_post_process
from oe.manifest import *
from oe.package_manager import *
import os
import shutil
import glob
import traceback


class Sdk(object, metaclass=ABCMeta):
    def __init__(self, d, manifest_dir):
        self.d = d
        self.sdk_output = self.d.getVar('SDK_OUTPUT', True)
        self.sdk_native_path = self.d.getVar('SDKPATHNATIVE', True).strip('/')
        self.target_path = self.d.getVar('SDKTARGETSYSROOT', True).strip('/')
        self.sysconfdir = self.d.getVar('sysconfdir', True).strip('/')

        self.sdk_target_sysroot = os.path.join(self.sdk_output, self.target_path)
        self.sdk_host_sysroot = self.sdk_output

        if manifest_dir is None:
            self.manifest_dir = self.d.getVar("SDK_DIR", True)
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
                         self.d.getVar('libdir_nativesdk', True).strip('/'),
                         "libGL*"))

        # Fix or remove broken .la files
        self.remove(os.path.join(self.sdk_output, self.sdk_native_path,
                         self.d.getVar('libdir_nativesdk', True).strip('/'),
                         "*.la"))

        # Link the ld.so.cache file into the hosts filesystem
        link_name = os.path.join(self.sdk_output, self.sdk_native_path,
                                 self.sysconfdir, "ld.so.cache")
        self.mkdirhier(os.path.dirname(link_name))
        os.symlink("/etc/ld.so.cache", link_name)

        execute_pre_post_process(self.d, self.d.getVar('SDK_POSTPROCESS_COMMAND', True))

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

class RpmSdk(Sdk):
    def __init__(self, d, manifest_dir=None):
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

        self.target_pm = RpmPM(d,
                               self.sdk_target_sysroot,
                               self.d.getVar('TARGET_VENDOR', True),
                               'target',
                               target_providename
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
                             self.d.getVar('SDK_VENDOR', True),
                             'host',
                             sdk_providename,
                             "SDK_PACKAGE_ARCHS",
                             "SDK_OS"
                             )

    def _populate_sysroot(self, pm, manifest):
        pkgs_to_install = manifest.parse_initial_manifest()

        pm.create_configs()
        pm.write_index()
        pm.dump_all_available_pkgs()
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
        bb.note("Installing TARGET packages")
        self._populate_sysroot(self.target_pm, self.target_manifest)

        self.target_pm.install_complementary(self.d.getVar('SDKIMAGE_INSTALL_COMPLEMENTARY', True))

        execute_pre_post_process(self.d, self.d.getVar("POPULATE_SDK_POST_TARGET_COMMAND", True))

        if not bb.utils.contains("SDKIMAGE_FEATURES", "package-management", True, False, self.d):
            self.target_pm.remove_packaging_data()

        bb.note("Installing NATIVESDK packages")
        self._populate_sysroot(self.host_pm, self.host_manifest)

        execute_pre_post_process(self.d, self.d.getVar("POPULATE_SDK_POST_HOST_COMMAND", True))

        if not bb.utils.contains("SDKIMAGE_FEATURES", "package-management", True, False, self.d):
            self.host_pm.remove_packaging_data()

        # Move host RPM library data
        native_rpm_state_dir = os.path.join(self.sdk_output,
                                            self.sdk_native_path,
                                            self.d.getVar('localstatedir_nativesdk', True).strip('/'),
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
        for f in glob.glob(os.path.join(self.sdk_output, "etc", "*")):
            self.movefile(f, native_sysconf_dir)
        self.remove(os.path.join(self.sdk_output, "etc"), True)


class OpkgSdk(Sdk):
    def __init__(self, d, manifest_dir=None):
        super(OpkgSdk, self).__init__(d, manifest_dir)

        self.target_conf = self.d.getVar("IPKGCONF_TARGET", True)
        self.host_conf = self.d.getVar("IPKGCONF_SDK", True)

        self.target_manifest = OpkgManifest(d, self.manifest_dir,
                                            Manifest.MANIFEST_TYPE_SDK_TARGET)
        self.host_manifest = OpkgManifest(d, self.manifest_dir,
                                          Manifest.MANIFEST_TYPE_SDK_HOST)

        self.target_pm = OpkgPM(d, self.sdk_target_sysroot, self.target_conf,
                                self.d.getVar("ALL_MULTILIB_PACKAGE_ARCHS", True))

        self.host_pm = OpkgPM(d, self.sdk_host_sysroot, self.host_conf,
                              self.d.getVar("SDK_PACKAGE_ARCHS", True))

    def _populate_sysroot(self, pm, manifest):
        pkgs_to_install = manifest.parse_initial_manifest()

        if (self.d.getVar('BUILD_IMAGES_FROM_FEEDS', True) or "") != "1":
            pm.write_index()

        pm.update()

        for pkg_type in self.install_order:
            if pkg_type in pkgs_to_install:
                pm.install(pkgs_to_install[pkg_type],
                           [False, True][pkg_type == Manifest.PKG_TYPE_ATTEMPT_ONLY])

    def _populate(self):
        bb.note("Installing TARGET packages")
        self._populate_sysroot(self.target_pm, self.target_manifest)

        self.target_pm.install_complementary(self.d.getVar('SDKIMAGE_INSTALL_COMPLEMENTARY', True))

        execute_pre_post_process(self.d, self.d.getVar("POPULATE_SDK_POST_TARGET_COMMAND", True))

        if not bb.utils.contains("SDKIMAGE_FEATURES", "package-management", True, False, self.d):
            self.target_pm.remove_packaging_data()

        bb.note("Installing NATIVESDK packages")
        self._populate_sysroot(self.host_pm, self.host_manifest)

        execute_pre_post_process(self.d, self.d.getVar("POPULATE_SDK_POST_HOST_COMMAND", True))

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
                                             self.d.getVar('localstatedir_nativesdk', True).strip('/'),
                                             "lib", "opkg")
        self.mkdirhier(native_opkg_state_dir)
        for f in glob.glob(os.path.join(self.sdk_output, "var", "lib", "opkg", "*")):
            self.movefile(f, native_opkg_state_dir)

        self.remove(os.path.join(self.sdk_output, "var"), True)


class DpkgSdk(Sdk):
    def __init__(self, d, manifest_dir=None):
        super(DpkgSdk, self).__init__(d, manifest_dir)

        self.target_conf_dir = os.path.join(self.d.getVar("APTCONF_TARGET", True), "apt")
        self.host_conf_dir = os.path.join(self.d.getVar("APTCONF_TARGET", True), "apt-sdk")

        self.target_manifest = DpkgManifest(d, self.manifest_dir,
                                            Manifest.MANIFEST_TYPE_SDK_TARGET)
        self.host_manifest = DpkgManifest(d, self.manifest_dir,
                                          Manifest.MANIFEST_TYPE_SDK_HOST)

        self.target_pm = DpkgPM(d, self.sdk_target_sysroot,
                                self.d.getVar("PACKAGE_ARCHS", True),
                                self.d.getVar("DPKG_ARCH", True),
                                self.target_conf_dir)

        self.host_pm = DpkgPM(d, self.sdk_host_sysroot,
                              self.d.getVar("SDK_PACKAGE_ARCHS", True),
                              self.d.getVar("DEB_SDK_ARCH", True),
                              self.host_conf_dir)

    def _copy_apt_dir_to(self, dst_dir):
        staging_etcdir_native = self.d.getVar("STAGING_ETCDIR_NATIVE", True)

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
        bb.note("Installing TARGET packages")
        self._populate_sysroot(self.target_pm, self.target_manifest)

        self.target_pm.install_complementary(self.d.getVar('SDKIMAGE_INSTALL_COMPLEMENTARY', True))

        execute_pre_post_process(self.d, self.d.getVar("POPULATE_SDK_POST_TARGET_COMMAND", True))

        self._copy_apt_dir_to(os.path.join(self.sdk_target_sysroot, "etc", "apt"))

        if not bb.utils.contains("SDKIMAGE_FEATURES", "package-management", True, False, self.d):
            self.target_pm.remove_packaging_data()

        bb.note("Installing NATIVESDK packages")
        self._populate_sysroot(self.host_pm, self.host_manifest)

        execute_pre_post_process(self.d, self.d.getVar("POPULATE_SDK_POST_HOST_COMMAND", True))

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
        sdk_output = d.getVar('SDK_OUTPUT', True)
        target_path = d.getVar('SDKTARGETSYSROOT', True).strip('/')

        rootfs_dir = [sdk_output, os.path.join(sdk_output, target_path)][target is True]

    img_type = d.getVar('IMAGE_PKGTYPE', True)
    if img_type == "rpm":
        arch_var = ["SDK_PACKAGE_ARCHS", None][target is True]
        os_var = ["SDK_OS", None][target is True]
        return RpmPkgsList(d, rootfs_dir, arch_var, os_var).list_pkgs()
    elif img_type == "ipk":
        conf_file_var = ["IPKGCONF_SDK", "IPKGCONF_TARGET"][target is True]
        return OpkgPkgsList(d, rootfs_dir, d.getVar(conf_file_var, True)).list_pkgs()
    elif img_type == "deb":
        return DpkgPkgsList(d, rootfs_dir).list_pkgs()

def populate_sdk(d, manifest_dir=None):
    env_bkp = os.environ.copy()

    img_type = d.getVar('IMAGE_PKGTYPE', True)
    if img_type == "rpm":
        RpmSdk(d, manifest_dir).populate()
    elif img_type == "ipk":
        OpkgSdk(d, manifest_dir).populate()
    elif img_type == "deb":
        DpkgSdk(d, manifest_dir).populate()

    os.environ.clear()
    os.environ.update(env_bkp)

if __name__ == "__main__":
    pass
