#
# SPDX-License-Identifier: MIT
#

import os
import shutil
import importlib
import unittest
from oeqa.selftest.case import OESelftestTestCase
from oeqa.selftest.cases.buildhistory import BuildhistoryBase
from oeqa.utils.commands import Command, runCmd, bitbake, get_bb_var, get_test_layer
from oeqa.utils import CommandError

class BuildhistoryDiffTests(BuildhistoryBase):

    def test_buildhistory_diff(self):
        target = 'xcursor-transparent-theme'
        self.run_buildhistory_operation(target, target_config="PR = \"r1\"", change_bh_location=True)
        self.run_buildhistory_operation(target, target_config="PR = \"r0\"", change_bh_location=False, expect_error=True)
        result = runCmd("oe-pkgdata-util read-value PKGV %s" % target)
        pkgv = result.output.rstrip()
        result = runCmd("buildhistory-diff -p %s" % get_bb_var('BUILDHISTORY_DIR'))
        expected_endlines = [
            "xcursor-transparent-theme-dev: RDEPENDS: removed \"xcursor-transparent-theme (['= %s-r1'])\", added \"xcursor-transparent-theme (['= %s-r0'])\"" % (pkgv, pkgv),
            "xcursor-transparent-theme-staticdev: RDEPENDS: removed \"xcursor-transparent-theme-dev (['= %s-r1'])\", added \"xcursor-transparent-theme-dev (['= %s-r0'])\"" % (pkgv, pkgv)
        ]
        for line in result.output.splitlines():
            for el in expected_endlines:
                if line.endswith(el):
                    expected_endlines.remove(el)
                    break
            else:
                self.fail('Unexpected line:\n%s\nExpected line endings:\n  %s' % (line, '\n  '.join(expected_endlines)))
        if expected_endlines:
            self.fail('Missing expected line endings:\n  %s' % '\n  '.join(expected_endlines))

@unittest.skipUnless(importlib.util.find_spec("cairo"), "Python cairo module is not present")
class OEScriptTests(OESelftestTestCase):

    @classmethod
    def setUpClass(cls):
        super(OEScriptTests, cls).setUpClass()
        import cairo
        bitbake("core-image-minimal -c rootfs -f")
        cls.tmpdir = get_bb_var('TMPDIR')
        cls.buildstats = cls.tmpdir + "/buildstats/" + sorted(os.listdir(cls.tmpdir + "/buildstats"))[-1]

    scripts_dir = os.path.join(get_bb_var('COREBASE'), 'scripts')

class OEPybootchartguyTests(OEScriptTests):

    def test_pybootchartguy_help(self):
        runCmd('%s/pybootchartgui/pybootchartgui.py  --help' % self.scripts_dir)

    def test_pybootchartguy_to_generate_build_png_output(self):
        runCmd('%s/pybootchartgui/pybootchartgui.py  %s -o %s/charts -f png' % (self.scripts_dir, self.buildstats, self.tmpdir))
        self.assertTrue(os.path.exists(self.tmpdir + "/charts.png"))

    def test_pybootchartguy_to_generate_build_svg_output(self):
        runCmd('%s/pybootchartgui/pybootchartgui.py  %s -o %s/charts -f svg' % (self.scripts_dir, self.buildstats, self.tmpdir))
        self.assertTrue(os.path.exists(self.tmpdir + "/charts.svg"))

    def test_pybootchartguy_to_generate_build_pdf_output(self):
        runCmd('%s/pybootchartgui/pybootchartgui.py  %s -o %s/charts -f pdf' % (self.scripts_dir, self.buildstats, self.tmpdir))
        self.assertTrue(os.path.exists(self.tmpdir + "/charts.pdf"))


class OEGitproxyTests(OESelftestTestCase):

    scripts_dir = os.path.join(get_bb_var('COREBASE'), 'scripts')

    def test_oegitproxy_help(self):
        try:
            res = runCmd('%s/oe-git-proxy  --help' % self.scripts_dir, assert_error=False)
            self.assertTrue(False)
        except CommandError as e:
            self.assertEqual(2, e.retcode)

    def run_oegitproxy(self, custom_shell=None):
        os.environ['SOCAT'] = shutil.which("echo")
        os.environ['ALL_PROXY'] = "https://proxy.example.com:3128"
        os.environ['NO_PROXY'] = "*.example.com,.no-proxy.org,192.168.42.0/24,127.*.*.*"

        if custom_shell is None:
            prefix = ''
        else:
            prefix = custom_shell + ' '

        # outside, use the proxy
        res = runCmd('%s%s/oe-git-proxy host.outside-example.com 9418' %
                     (prefix,self.scripts_dir))
        self.assertIn('PROXY:', res.output)
        # match with wildcard suffix
        res = runCmd('%s%s/oe-git-proxy host.example.com 9418' %
                     (prefix, self.scripts_dir))
        self.assertIn('TCP:', res.output)
        # match just suffix
        res = runCmd('%s%s/oe-git-proxy host.no-proxy.org 9418' %
                     (prefix, self.scripts_dir))
        self.assertIn('TCP:', res.output)
        # match IP subnet
        res = runCmd('%s%s/oe-git-proxy 192.168.42.42 9418' %
                     (prefix, self.scripts_dir))
        self.assertIn('TCP:', res.output)
        # match IP wildcard
        res = runCmd('%s%s/oe-git-proxy 127.1.2.3 9418' %
                     (prefix, self.scripts_dir))
        self.assertIn('TCP:', res.output)
        
        # test that * globbering is off
        os.environ['NO_PROXY'] = "*"
        res = runCmd('%s%s/oe-git-proxy host.example.com 9418' %
                     (prefix, self.scripts_dir))
        self.assertIn('TCP:', res.output)

    def test_oegitproxy_proxy(self):
        self.run_oegitproxy()

    def test_oegitproxy_proxy_dash(self):
        dash = shutil.which("dash")
        if dash is None:
            self.skipTest("No \"dash\" found on test system.")
        self.run_oegitproxy(custom_shell=dash)

class OeRunNativeTest(OESelftestTestCase):
    def test_oe_run_native(self):
        bitbake("qemu-helper-native -c addto_recipe_sysroot")
        result = runCmd("oe-run-native qemu-helper-native tunctl -h")
        self.assertIn("Delete: tunctl -d device-name [-f tun-clone-device]", result.output)

class OEListPackageconfigTests(OEScriptTests):
    #oe-core.scripts.List_all_the_PACKAGECONFIG's_flags
    def check_endlines(self, results,  expected_endlines): 
        for line in results.output.splitlines():
            for el in expected_endlines:
                if line.split() == el.split():
                    expected_endlines.remove(el)
                    break

        if expected_endlines:
            self.fail('Missing expected listings:\n  %s' % '\n  '.join(expected_endlines))


    #oe-core.scripts.List_all_the_PACKAGECONFIG's_flags
    def test_packageconfig_flags_help(self):
        runCmd('%s/contrib/list-packageconfig-flags.py -h' % self.scripts_dir)

    def test_packageconfig_flags_default(self):
        results = runCmd('%s/contrib/list-packageconfig-flags.py' % self.scripts_dir)
        expected_endlines = []
        expected_endlines.append("RECIPE NAME                  PACKAGECONFIG FLAGS")
        expected_endlines.append("pinentry                     gtk2 libcap ncurses qt secret")
        expected_endlines.append("tar                          acl")

        self.check_endlines(results, expected_endlines)


    def test_packageconfig_flags_option_flags(self):
        results = runCmd('%s/contrib/list-packageconfig-flags.py -f' % self.scripts_dir)
        expected_endlines = []
        expected_endlines.append("PACKAGECONFIG FLAG     RECIPE NAMES")
        expected_endlines.append("qt                     nativesdk-pinentry  pinentry  pinentry-native")
        expected_endlines.append("secret                 nativesdk-pinentry  pinentry  pinentry-native")

        self.check_endlines(results, expected_endlines)

    def test_packageconfig_flags_option_all(self):
        results = runCmd('%s/contrib/list-packageconfig-flags.py -a' % self.scripts_dir)
        expected_endlines = []
        expected_endlines.append("pinentry-1.1.1")
        expected_endlines.append("PACKAGECONFIG ncurses libcap")
        expected_endlines.append("PACKAGECONFIG[qt] --enable-pinentry-qt, --disable-pinentry-qt, qtbase-native qtbase")
        expected_endlines.append("PACKAGECONFIG[gtk2] --enable-pinentry-gtk2, --disable-pinentry-gtk2, gtk+ glib-2.0")
        expected_endlines.append("PACKAGECONFIG[libcap] --with-libcap, --without-libcap, libcap")
        expected_endlines.append("PACKAGECONFIG[ncurses] --enable-ncurses  --with-ncurses-include-dir=${STAGING_INCDIR}, --disable-ncurses, ncurses")
        expected_endlines.append("PACKAGECONFIG[secret] --enable-libsecret, --disable-libsecret, libsecret")

        self.check_endlines(results, expected_endlines)

    def test_packageconfig_flags_options_preferred_only(self):
        results = runCmd('%s/contrib/list-packageconfig-flags.py -p' % self.scripts_dir)
        expected_endlines = []
        expected_endlines.append("RECIPE NAME                  PACKAGECONFIG FLAGS")
        expected_endlines.append("pinentry                     gtk2 libcap ncurses qt secret")

        self.check_endlines(results, expected_endlines)

