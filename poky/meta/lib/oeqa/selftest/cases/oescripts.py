#
# SPDX-License-Identifier: MIT
#

import os
import unittest
from oeqa.selftest.case import OESelftestTestCase
from oeqa.selftest.cases.buildhistory import BuildhistoryBase
from oeqa.utils.commands import Command, runCmd, bitbake, get_bb_var, get_test_layer

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

class OEScriptTests(OESelftestTestCase):

    @classmethod
    def setUpClass(cls):
        super(OEScriptTests, cls).setUpClass()
        try:
            import cairo
        except ImportError:
            raise unittest.SkipTest('Python module cairo is not present')
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

