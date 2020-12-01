#
# SPDX-License-Identifier: GPL-2.0-only
#

import glob
from oe.utils import execute_pre_post_process
from oe.sdk import Sdk
from oe.manifest import Manifest
from oe.package_manager.rpm.manifest import PkgManifest
from oe.package_manager.rpm import RpmPM

class PkgSdk(Sdk):
    def __init__(self, d, manifest_dir=None, rpm_workdir="oe-sdk-repo"):
        super(PkgSdk, self).__init__(d, manifest_dir)

        self.target_manifest = PkgManifest(d, self.manifest_dir,
                                           Manifest.MANIFEST_TYPE_SDK_TARGET)
        self.host_manifest = PkgManifest(d, self.manifest_dir,
                                         Manifest.MANIFEST_TYPE_SDK_HOST)

        rpm_repo_workdir = "oe-sdk-repo"
        if "sdk_ext" in d.getVar("BB_RUNTASK"):
            rpm_repo_workdir = "oe-sdk-ext-repo"

        self.target_pm = RpmPM(d,
                               self.sdk_target_sysroot,
                               self.d.getVar('TARGET_VENDOR'),
                               'target',
                               rpm_repo_workdir=rpm_repo_workdir
                               )

        self.host_pm = RpmPM(d,
                             self.sdk_host_sysroot,
                             self.d.getVar('SDK_VENDOR'),
                             'host',
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
