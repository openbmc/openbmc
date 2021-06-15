#
# SPDX-License-Identifier: GPL-2.0-only
#

import glob
import shutil
from oe.utils import execute_pre_post_process
from oe.sdk import Sdk
from oe.package_manager.ipk.manifest import PkgManifest
from oe.manifest import Manifest
from oe.package_manager.ipk import OpkgPM

class PkgSdk(Sdk):
    def __init__(self, d, manifest_dir=None):
        super(PkgSdk, self).__init__(d, manifest_dir)

        # In sdk_list_installed_packages the call to opkg is hardcoded to
        # always use IPKGCONF_TARGET and there's no exposed API to change this
        # so simply override IPKGCONF_TARGET to use this separated config file.
        ipkgconf_sdk_target = d.getVar("IPKGCONF_SDK_TARGET")
        d.setVar("IPKGCONF_TARGET", ipkgconf_sdk_target)

        self.target_conf = self.d.getVar("IPKGCONF_TARGET")
        self.host_conf = self.d.getVar("IPKGCONF_SDK")

        self.target_manifest = PkgManifest(d, self.manifest_dir,
                                            Manifest.MANIFEST_TYPE_SDK_TARGET)
        self.host_manifest = PkgManifest(d, self.manifest_dir,
                                          Manifest.MANIFEST_TYPE_SDK_HOST)

        ipk_repo_workdir = "oe-sdk-repo"
        if "sdk_ext" in d.getVar("BB_RUNTASK"):
            ipk_repo_workdir = "oe-sdk-ext-repo"

        self.target_pm = OpkgPM(d, self.sdk_target_sysroot, self.target_conf,
                                self.d.getVar("ALL_MULTILIB_PACKAGE_ARCHS"),
                                ipk_repo_workdir=ipk_repo_workdir)

        self.host_pm = OpkgPM(d, self.sdk_host_sysroot, self.host_conf,
                              self.d.getVar("SDK_PACKAGE_ARCHS"),
                                ipk_repo_workdir=ipk_repo_workdir)

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

        self.target_pm.run_intercepts(populate_sdk='target')

        execute_pre_post_process(self.d, self.d.getVar("POPULATE_SDK_POST_TARGET_COMMAND"))

        if not bb.utils.contains("SDKIMAGE_FEATURES", "package-management", True, False, self.d):
            self.target_pm.remove_packaging_data()

        bb.note("Installing NATIVESDK packages")
        self._populate_sysroot(self.host_pm, self.host_manifest)
        self.install_locales(self.host_pm)

        self.host_pm.run_intercepts(populate_sdk='host')

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
