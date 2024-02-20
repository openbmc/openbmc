#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

import os
import re

import oeqa.utils.ftools as ftools
from oeqa.utils.commands import runCmd, get_bb_var, get_bb_vars, bitbake

from oeqa.selftest.case import OESelftestTestCase

class BitbakeLayers(OESelftestTestCase):

    @classmethod
    def setUpClass(cls):
        super(BitbakeLayers, cls).setUpClass()
        bitbake("python3-jsonschema-native")
        bitbake("-c addto_recipe_sysroot python3-jsonschema-native")

    def test_bitbakelayers_layerindexshowdepends(self):
        result = runCmd('bitbake-layers layerindex-show-depends meta-poky')
        find_in_contents = re.search("openembedded-core", result.output)
        self.assertTrue(find_in_contents, msg = "openembedded-core should have been listed at this step. bitbake-layers layerindex-show-depends meta-poky output: %s" % result.output)

    def test_bitbakelayers_showcrossdepends(self):
        result = runCmd('bitbake-layers show-cross-depends')
        self.assertIn('aspell', result.output)

    def test_bitbakelayers_showlayers(self):
        result = runCmd('bitbake-layers show-layers')
        self.assertIn('meta-selftest', result.output)

    def test_bitbakelayers_showappends(self):
        recipe = "xcursor-transparent-theme"
        bb_file = self.get_recipe_basename(recipe)
        result = runCmd('bitbake-layers show-appends')
        self.assertIn(bb_file, result.output)

    def test_bitbakelayers_showoverlayed(self):
        result = runCmd('bitbake-layers show-overlayed')
        self.assertIn('aspell', result.output)

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
        find_in_contents = re.search(r"##### bbappended from meta-selftest #####\n(.*\n)*include test_recipe.inc", contents)
        self.assertTrue(find_in_contents, msg = "Flattening layers did not work. bitbake-layers flatten output: %s" % result.output)

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
        result = runCmd('bitbake-layers show-recipes -i meson,pkgconfig')
        self.assertIn('libproxy:', result.output)
        result = runCmd('bitbake-layers show-recipes -i cmake,pkgconfig')
        self.assertNotIn('mtd-utils:', result.output) # doesn't inherit either
        self.assertNotIn('wget:', result.output) # doesn't inherit cmake
        self.assertNotIn('waffle:', result.output) # doesn't inherit pkgconfig
        result = runCmd('bitbake-layers show-recipes -i nonexistentclass', ignore_status=True)
        self.assertNotEqual(result.status, 0, 'bitbake-layers show-recipes -i nonexistentclass should have failed')
        self.assertIn('ERROR:', result.output)

    def test_bitbakelayers_createlayer(self):
        priority = 10
        layername = 'test-bitbakelayer-layercreate'
        layerpath = os.path.join(self.builddir, layername)
        self.assertFalse(os.path.exists(layerpath), '%s should not exist at this point in time' % layerpath)
        result = runCmd('bitbake-layers create-layer --priority=%d %s' % (priority, layerpath))
        self.track_for_cleanup(layerpath)
        result = runCmd('bitbake-layers add-layer %s' % layerpath)
        self.add_command_to_tearDown('bitbake-layers remove-layer %s' % layerpath)
        result = runCmd('bitbake-layers show-layers')
        find_in_contents = re.search(re.escape(layername) + r'\s+' + re.escape(layerpath) + r'\s+' + re.escape(str(priority)), result.output)
        self.assertTrue(find_in_contents, "%s not found in layers\n%s" % (layername, result.output))

        layervars = ['BBFILE_PRIORITY', 'BBFILE_PATTERN', 'LAYERDEPENDS', 'LAYERSERIES_COMPAT']
        bb_vars = get_bb_vars(['BBFILE_COLLECTIONS'] + ['%s_%s' % (v, layername) for v in layervars])

        for v in layervars:
            varname = '%s_%s' % (v, layername)
            self.assertIsNotNone(bb_vars[varname], "%s not found" % varname)

        find_in_contents = re.search(r'(^|\s)' + re.escape(layername) + r'($|\s)', bb_vars['BBFILE_COLLECTIONS'])
        self.assertTrue(find_in_contents, "%s not in BBFILE_COLLECTIONS" % layername)

        self.assertEqual(bb_vars['BBFILE_PRIORITY_%s' % layername], str(priority), 'BBFILE_PRIORITY_%s != %d' % (layername, priority))

        result = runCmd('bitbake-layers save-build-conf {} {}'.format(layerpath, "buildconf-1"))
        for f in ('local.conf.sample', 'bblayers.conf.sample', 'conf-summary.txt', 'conf-notes.txt'):
            fullpath = os.path.join(layerpath, "conf", "templates", "buildconf-1", f)
            self.assertTrue(os.path.exists(fullpath), "Template configuration file {} not found".format(fullpath))

    def get_recipe_basename(self, recipe):
        recipe_file = ""
        result = runCmd("bitbake-layers show-recipes -f %s" % recipe)
        for line in result.output.splitlines():
            if recipe in line:
                recipe_file = line
                break

        self.assertTrue(os.path.isfile(recipe_file), msg = "Can't find recipe file for %s" % recipe)
        return os.path.basename(recipe_file)

    def validate_layersjson(self, json):
        python = os.path.join(get_bb_var('STAGING_BINDIR', 'python3-jsonschema-native'), 'nativepython3')
        jsonvalidator = os.path.join(get_bb_var('STAGING_BINDIR', 'python3-jsonschema-native'), 'jsonschema')
        jsonschema = os.path.join(get_bb_var('COREBASE'), 'meta/files/layers.schema.json')
        result = runCmd("{} {} -i {} {}".format(python, jsonvalidator, json, jsonschema))

    def test_validate_examplelayersjson(self):
        json = os.path.join(get_bb_var('COREBASE'), "meta/files/layers.example.json")
        self.validate_layersjson(json)

    def test_bitbakelayers_setup(self):
        result = runCmd('bitbake-layers create-layers-setup {}'.format(self.testlayer_path))
        jsonfile = os.path.join(self.testlayer_path, "setup-layers.json")
        self.validate_layersjson(jsonfile)

        # The revision-under-test may not necessarily be available on the remote server,
        # so replace it with a revision that has a yocto-4.1 tag.
        import json
        with open(jsonfile) as f:
            data = json.load(f)
        for s in data['sources']:
            data['sources'][s]['git-remote']['rev'] = '5200799866b92259e855051112520006e1aaaac0'
        with open(jsonfile, 'w') as f:
            json.dump(data, f)

        testcheckoutdir = os.path.join(self.builddir, 'test-layer-checkout')
        result = runCmd('{}/setup-layers --destdir {}'.format(self.testlayer_path, testcheckoutdir))
        layers_json = os.path.join(testcheckoutdir, ".oe-layers.json")
        self.assertTrue(os.path.exists(layers_json), "File {} not found in test layer checkout".format(layers_json))

        # As setup-layers checkout out an old revision of poky, there is no setup-build symlink,
        # and we need to run oe-setup-build directly from the current poky tree under test
        oe_setup_build = os.path.join(get_bb_var('COREBASE'), 'scripts/oe-setup-build')
        oe_setup_build_l = os.path.join(testcheckoutdir, 'setup-build')
        os.symlink(oe_setup_build,oe_setup_build_l)

        cmd = '{} --layerlist {} list -v'.format(oe_setup_build_l, layers_json)
        result = runCmd(cmd)
        cond = "conf/templates/default" in result.output
        self.assertTrue(cond, "Incorrect output from {}: {}".format(cmd, result.output))

        # rather than hardcode the build setup cmdline here, let's actually run what the tool suggests to the user
        conf = None
        if 'poky-default' in result.output:
            conf = 'poky-default'
        elif 'meta-default' in result.output:
            conf = 'meta-default'
        self.assertIsNotNone(conf, "Could not find the configuration to set up a build in the output: {}".format(result.output))

        cmd = '{} --layerlist {} setup -c {} --no-shell'.format(oe_setup_build_l, layers_json, conf)
        result = runCmd(cmd)

    def test_bitbakelayers_updatelayer(self):
        result = runCmd('bitbake-layers create-layers-setup {}'.format(self.testlayer_path))
        jsonfile = os.path.join(self.testlayer_path, "setup-layers.json")
        self.validate_layersjson(jsonfile)

        import json
        with open(jsonfile) as f:
            data = json.load(f)
        repos = []
        for s in data['sources']:
            repos.append(s)

        self.assertTrue(len(repos) > 1, "Not enough repositories available")
        self.validate_layersjson(jsonfile)

        test_ref_1 = 'ref_1'
        test_ref_2 = 'ref_2'

        # Create a new layers setup using custom references
        result = runCmd('bitbake-layers create-layers-setup --use-custom-reference {first_repo}:{test_ref} --use-custom-reference {second_repo}:{test_ref} {path}'
                        .format(first_repo=repos[0], second_repo=repos[1], test_ref=test_ref_1, path=self.testlayer_path))
        self.validate_layersjson(jsonfile)

        with open(jsonfile) as f:
            data = json.load(f)
        first_rev_1 = data['sources'][repos[0]]['git-remote']['rev']
        first_desc_1 = data['sources'][repos[0]]['git-remote']['describe']
        second_rev_1 = data['sources'][repos[1]]['git-remote']['rev']
        second_desc_1 = data['sources'][repos[1]]['git-remote']['describe']

        self.assertEqual(first_rev_1, test_ref_1, "Revision not set correctly: '{}'".format(first_rev_1))
        self.assertEqual(first_desc_1, '', "Describe not cleared: '{}'".format(first_desc_1))
        self.assertEqual(second_rev_1, test_ref_1, "Revision not set correctly: '{}'".format(second_rev_1))
        self.assertEqual(second_desc_1, '', "Describe not cleared: '{}'".format(second_desc_1))

        # Update one of the repositories in the layers setup using a different custom reference
        # This should only update the selected repository, everything else should remain as is
        result = runCmd('bitbake-layers create-layers-setup --update --use-custom-reference {first_repo}:{test_ref} {path}'
                        .format(first_repo=repos[0], test_ref=test_ref_2, path=self.testlayer_path))
        self.validate_layersjson(jsonfile)

        with open(jsonfile) as f:
            data = json.load(f)
        first_rev_2 = data['sources'][repos[0]]['git-remote']['rev']
        first_desc_2 = data['sources'][repos[0]]['git-remote']['describe']
        second_rev_2 = data['sources'][repos[1]]['git-remote']['rev']
        second_desc_2 = data['sources'][repos[1]]['git-remote']['describe']

        self.assertEqual(first_rev_2, test_ref_2, "Revision not set correctly: '{}'".format(first_rev_2))
        self.assertEqual(first_desc_2, '', "Describe not cleared: '{}'".format(first_desc_2))
        self.assertEqual(second_rev_2, second_rev_1, "Revision should not be updated: '{}'".format(second_rev_2))
        self.assertEqual(second_desc_2, second_desc_1, "Describe should not be updated: '{}'".format(second_desc_2))
