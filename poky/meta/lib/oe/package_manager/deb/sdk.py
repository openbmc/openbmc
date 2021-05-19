#
# SPDX-License-Identifier: GPL-2.0-only
#

import glob
import shutil
from oe.utils import execute_pre_post_process
from oe.sdk import Sdk
from oe.manifest import Manifest
from oe.package_manager.deb import DpkgPM
from oe.package_manager.deb.manifest import PkgManifest

class PkgSdk(Sdk):
    def __init__(self, d, manifest_dir=None):
        super(PkgSdk, self).__init__(d, manifest_dir)

        self.target_conf_dir = os.path.join(self.d.getVar("APTCONF_TARGET"), "apt")
        self.host_conf_dir = os.path.join(self.d.getVar("APTCONF_TARGET"), "apt-sdk")


        self.target_manifest = PkgManifest(d, self.manifest_dir,
                                            Manifest.MANIFEST_TYPE_SDK_TARGET)
        self.host_manifest = PkgManifest(d, self.manifest_dir,
                                          Manifest.MANIFEST_TYPE_SDK_HOST)

        deb_repo_workdir = "oe-sdk-repo"
        if "sdk_ext" in d.getVar("BB_RUNTASK"):
            deb_repo_workdir = "oe-sdk-ext-repo"

        self.target_pm = DpkgPM(d, self.sdk_target_sysroot,
                                self.d.getVar("PACKAGE_ARCHS"),
                                self.d.getVar("DPKG_ARCH"),
                                self.target_conf_dir,
                                deb_repo_workdir=deb_repo_workdir)

        self.host_pm = DpkgPM(d, self.sdk_host_sysroot,
                              self.d.getVar("SDK_PACKAGE_ARCHS"),
                              self.d.getVar("DEB_SDK_ARCH"),
                              self.host_conf_dir,
                              deb_repo_workdir=deb_repo_workdir)

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

        self.target_pm.run_pre_post_installs()

        self.target_pm.run_intercepts(populate_sdk='target')

        execute_pre_post_process(self.d, self.d.getVar("POPULATE_SDK_POST_TARGET_COMMAND"))

        self._copy_apt_dir_to(os.path.join(self.sdk_target_sysroot, "etc", "apt"))

        if not bb.utils.contains("SDKIMAGE_FEATURES", "package-management", True, False, self.d):
            self.target_pm.remove_packaging_data()

        bb.note("Installing NATIVESDK packages")
        self._populate_sysroot(self.host_pm, self.host_manifest)
        self.install_locales(self.host_pm)

        self.host_pm.run_pre_post_installs()

        self.host_pm.run_intercepts(populate_sdk='host')

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
