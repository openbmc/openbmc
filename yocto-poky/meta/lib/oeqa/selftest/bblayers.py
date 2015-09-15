import unittest
import os
import logging
import re
import shutil

import oeqa.utils.ftools as ftools
from oeqa.selftest.base import oeSelfTest
from oeqa.utils.commands import runCmd, get_bb_var
from oeqa.utils.decorators import testcase

class BitbakeLayers(oeSelfTest):

    @testcase(756)
    def test_bitbakelayers_showcrossdepends(self):
        result = runCmd('bitbake-layers show-cross-depends')
        self.assertTrue('aspell' in result.output, msg = "No dependencies were shown. bitbake-layers show-cross-depends output: %s" % result.output)

    @testcase(83)
    def test_bitbakelayers_showlayers(self):
        result = runCmd('bitbake-layers show-layers')
        self.assertTrue('meta-selftest' in result.output, msg = "No layers were shown. bitbake-layers show-layers output: %s" % result.output)

    @testcase(93)
    def test_bitbakelayers_showappends(self):
        result = runCmd('bitbake-layers show-appends')
        self.assertTrue('xcursor-transparent-theme_0.1.1.bbappend' in result.output, msg="xcursor-transparent-theme_0.1.1.bbappend file was not recognised.  bitbake-layers show-appends output: %s" % result.output)

    @testcase(90)
    def test_bitbakelayers_showoverlayed(self):
        result = runCmd('bitbake-layers show-overlayed')
        self.assertTrue('aspell' in result.output, msg="aspell overlayed recipe was not recognised bitbake-layers show-overlayed %s" % result.output)

    @testcase(95)
    def test_bitbakelayers_flatten(self):
        testoutdir = os.path.join(self.builddir, 'test_bitbakelayers_flatten')
        self.assertFalse(os.path.isdir(testoutdir), msg = "test_bitbakelayers_flatten should not exist at this point in time")
        self.track_for_cleanup(testoutdir)
        result = runCmd('bitbake-layers flatten %s' % testoutdir)
        bb_file = os.path.join(testoutdir, 'recipes-graphics/xcursor-transparent-theme/xcursor-transparent-theme_0.1.1.bb')
        self.assertTrue(os.path.isfile(bb_file), msg = "Cannot find xcursor-transparent-theme_0.1.1.bb in the test_bitbakelayers_flatten local dir.")
        contents = ftools.read_file(bb_file)
        find_in_contents = re.search("##### bbappended from meta-selftest #####\n(.*\n)*include test_recipe.inc", contents)
        self.assertTrue(find_in_contents, msg = "Flattening layers did not work. bitbake-layers flatten output: %s" % result.output)

    @testcase(1195)
    def test_bitbakelayers_add_remove(self):
        test_layer = os.path.join(get_bb_var('COREBASE'), 'meta-skeleton')
        result = runCmd('bitbake-layers show-layers')
        self.assertNotIn('meta-skeleton', result.output, "This test cannot run with meta-skeleton in bblayers.conf. bitbake-layers show-layers output: %s" % result.output)
        result = runCmd('bitbake-layers add-layer %s' % test_layer)
        result = runCmd('bitbake-layers show-layers')
        self.assertIn('meta-skeleton', result.output, msg = "Something wrong happened. meta-skeleton layer was not added to conf/bblayers.conf.  bitbake-layers show-layers output: %s" % result.output)
        result = runCmd('bitbake-layers remove-layer %s' % test_layer)
        result = runCmd('bitbake-layers show-layers')
        self.assertNotIn('meta-skeleton', result.output, msg = "meta-skeleton should have been removed at this step.  bitbake-layers show-layers output: %s" % result.output)
        result = runCmd('bitbake-layers add-layer %s' % test_layer)
        result = runCmd('bitbake-layers show-layers')
        self.assertIn('meta-skeleton', result.output, msg = "Something wrong happened. meta-skeleton layer was not added to conf/bblayers.conf.  bitbake-layers show-layers output: %s" % result.output)
        result = runCmd('bitbake-layers remove-layer */meta-skeleton')
        result = runCmd('bitbake-layers show-layers')
        self.assertNotIn('meta-skeleton', result.output, msg = "meta-skeleton should have been removed at this step.  bitbake-layers show-layers output: %s" % result.output)
