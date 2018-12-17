import unittest

from oeqa.sdk.case import OESDKTestCase
from oeqa.sdk.utils.sdkbuildproject import SDKBuildProject

class GalculatorTest(OESDKTestCase):
    td_vars = ['DATETIME']

    @classmethod
    def setUpClass(self):
        if not (self.tc.hasTargetPackage("gtk+3", multilib=True) or \
                self.tc.hasTargetPackage("libgtk-3.0", multilib=True)):
            raise unittest.SkipTest("GalculatorTest class: SDK don't support gtk+3")
        if not (self.tc.hasHostPackage("nativesdk-gettext-dev")):
            raise unittest.SkipTest("GalculatorTest class: SDK doesn't contain gettext")

    def test_galculator(self):
        dl_dir = self.td.get('DL_DIR', None)
        project = None
        try:
            project = SDKBuildProject(self.tc.sdk_dir + "/galculator/",
                                      self.tc.sdk_env,
                                      "http://galculator.mnim.org/downloads/galculator-2.1.4.tar.bz2",
                                      self.tc.sdk_dir, self.td['DATETIME'], dl_dir=dl_dir)

            project.download_archive()

            # regenerate configure to get support for --with-libtool-sysroot
            legacy_preconf=("autoreconf -i -f -I ${OECORE_TARGET_SYSROOT}/usr/share/aclocal -I m4;")

            self.assertEqual(project.run_configure(extra_cmds=legacy_preconf),
                             0, msg="Running configure failed")

            self.assertEqual(project.run_make(), 0,
                            msg="Running make failed")
        finally:
            project.clean()
