import os
import re

import oeqa.utils.ftools as ftools
from oeqa.utils.commands import runCmd, get_bb_var

from oeqa.selftest.case import OESelftestTestCase
from oeqa.core.decorator.oeid import OETestID

class BitbakeLayers(OESelftestTestCase):

    @OETestID(756)
    def test_bitbakelayers_showcrossdepends(self):
        result = runCmd('bitbake-layers show-cross-depends')
        self.assertTrue('aspell' in result.output, msg = "No dependencies were shown. bitbake-layers show-cross-depends output: %s" % result.output)

    @OETestID(83)
    def test_bitbakelayers_showlayers(self):
        result = runCmd('bitbake-layers show-layers')
        self.assertTrue('meta-selftest' in result.output, msg = "No layers were shown. bitbake-layers show-layers output: %s" % result.output)

    @OETestID(93)
    def test_bitbakelayers_showappends(self):
        recipe = "xcursor-transparent-theme"
        bb_file = self.get_recipe_basename(recipe)
        result = runCmd('bitbake-layers show-appends')
        self.assertTrue(bb_file in result.output, msg="%s file was not recognised. bitbake-layers show-appends output: %s" % (bb_file, result.output))

    @OETestID(90)
    def test_bitbakelayers_showoverlayed(self):
        result = runCmd('bitbake-layers show-overlayed')
        self.assertTrue('aspell' in result.output, msg="aspell overlayed recipe was not recognised bitbake-layers show-overlayed %s" % result.output)

    @OETestID(95)
    def test_bitbakelayers_flatten(self):
        recipe = "xcursor-transparent-theme"
        recipe_path = "recipes-graphics/xcursor-transparent-theme"
        recipe_file = self.get_recipe_basename(recipe)
        testoutdir = os.path.join(self.builddir, 'test_bitbakelayers_flatten')
        self.assertFalse(os.path.isdir(testoutdir), msg = "test_bitbakelayers_flatten should not exist at this point in time")
        self.track_for_cleanup(testoutdir)
        result = runCmd('bitbake-layers flatten %s' % testoutdir)
        bb_file = os.path.join(testoutdir, recipe_path, recipe_file)
        self.assertTrue(os.path.isfile(bb_file), msg = "Cannot find xcursor-transparent-theme_0.1.1.bb in the test_bitbakelayers_flatten local dir.")
        contents = ftools.read_file(bb_file)
        find_in_contents = re.search("##### bbappended from meta-selftest #####\n(.*\n)*include test_recipe.inc", contents)
        self.assertTrue(find_in_contents, msg = "Flattening layers did not work. bitbake-layers flatten output: %s" % result.output)

    @OETestID(1195)
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

    @OETestID(1384)
    def test_bitbakelayers_showrecipes(self):
        result = runCmd('bitbake-layers show-recipes')
        self.assertIn('aspell:', result.output)
        self.assertIn('mtd-utils:', result.output)
        self.assertIn('core-image-minimal:', result.output)
        result = runCmd('bitbake-layers show-recipes mtd-utils')
        self.assertIn('mtd-utils:', result.output)
        self.assertNotIn('aspell:', result.output)
        result = runCmd('bitbake-layers show-recipes -i image')
        self.assertIn('core-image-minimal', result.output)
        self.assertNotIn('mtd-utils:', result.output)
        result = runCmd('bitbake-layers show-recipes -i cmake,pkgconfig')
        self.assertIn('libproxy:', result.output)
        self.assertNotIn('mtd-utils:', result.output) # doesn't inherit either
        self.assertNotIn('wget:', result.output) # doesn't inherit cmake
        self.assertNotIn('waffle:', result.output) # doesn't inherit pkgconfig
        result = runCmd('bitbake-layers show-recipes -i nonexistentclass', ignore_status=True)
        self.assertNotEqual(result.status, 0, 'bitbake-layers show-recipes -i nonexistentclass should have failed')
        self.assertIn('ERROR:', result.output)

    def get_recipe_basename(self, recipe):
        recipe_file = ""
        result = runCmd("bitbake-layers show-recipes -f %s" % recipe)
        for line in result.output.splitlines():
            if recipe in line:
                recipe_file = line
                break

        self.assertTrue(os.path.isfile(recipe_file), msg = "Can't find recipe file for %s" % recipe)
        return os.path.basename(recipe_file)
