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

        # Fetch variables used in multiple test cases
        bb_vars = get_bb_vars(['COREBASE', 'BITBAKEPATH'])
        cls.corebase = bb_vars['COREBASE']
        cls.bitbakepath = bb_vars['BITBAKEPATH']
        cls.jsonschema_staging_bindir = get_bb_var('STAGING_BINDIR', 'python3-jsonschema-native')

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
        test_layer = os.path.join(self.corebase, 'meta-skeleton')
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

    def validate_json(self, json, jsonschema):
        python = os.path.join(self.jsonschema_staging_bindir, 'nativepython3')
        jsonvalidator = os.path.join(self.jsonschema_staging_bindir, 'jsonschema')
        schemas_dir = os.path.join(self.bitbakepath, "..", "setup-schema")
        if not os.path.isabs(jsonschema):
            jsonschema = os.path.join(schemas_dir, jsonschema)

        result = runCmd(
            "{} {} -i {} --base-uri file://{}/ {}".format(
                python, jsonvalidator, json, schemas_dir, jsonschema
            )
        )

    def validate_layersjson(self, json):
        self.validate_json(json, "layers.schema.json")

    def test_validate_examplelayersjson(self):
        json = os.path.join(self.corebase, "meta/files/layers.example.json")
        self.validate_layersjson(json)

    def test_validate_bitbake_setup_default_registry(self):
        jsonschema = "bitbake-setup.schema.json"

        default_registry_path = os.path.join(self.bitbakepath, "..", "default-registry", "configurations")

        for root, _, files in os.walk(default_registry_path):
            for f in files:
                if not f.endswith(".conf.json"):
                    continue
                json = os.path.join(root, f)
                self.logger.debug("Validating %s", json)
                self.validate_json(json, jsonschema)

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
            if s == 'poky' or s == 'build':
                data['sources'][s]['git-remote']['rev'] = '5200799866b92259e855051112520006e1aaaac0'
            elif s == 'meta-yocto':
                data['sources'][s]['git-remote']['rev'] = '913bd8ba4dd1d5d2a38261bde985b64a36e36281'
            elif s == 'openembedded-core':
                data['sources'][s]['git-remote']['rev'] = '744a2277844ec9a384a9ca7dae2a634d5a0d3590'
        with open(jsonfile, 'w') as f:
            json.dump(data, f)

        testcheckoutdir = os.path.join(self.builddir, 'test-layer-checkout')
        result = runCmd('{}/setup-layers --destdir {}'.format(self.testlayer_path, testcheckoutdir))
        layers_json = os.path.join(testcheckoutdir, ".oe-layers.json")
        self.assertTrue(os.path.exists(layers_json), "File {} not found in test layer checkout".format(layers_json))

        # As setup-layers checkout out an old revision of poky, there is no setup-build symlink,
        # and we need to run oe-setup-build directly from the current poky tree under test
        oe_setup_build = os.path.join(self.corebase, 'scripts/oe-setup-build')
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

class BitbakeConfigBuild(OESelftestTestCase):
    def test_enable_disable_fragments(self):
        self.assertEqual(get_bb_var('SELFTEST_FRAGMENT_VARIABLE'), None)
        self.assertEqual(get_bb_var('SELFTEST_FRAGMENT_ANOTHER_VARIABLE'), None)

        runCmd('bitbake-config-build enable-fragment selftest/test-fragment')
        self.assertEqual(get_bb_var('SELFTEST_FRAGMENT_VARIABLE'), 'somevalue')
        self.assertEqual(get_bb_var('SELFTEST_FRAGMENT_ANOTHER_VARIABLE'), None)

        runCmd('bitbake-config-build enable-fragment selftest/more-fragments-here/test-another-fragment')
        self.assertEqual(get_bb_var('SELFTEST_FRAGMENT_VARIABLE'), 'somevalue')
        self.assertEqual(get_bb_var('SELFTEST_FRAGMENT_ANOTHER_VARIABLE'), 'someothervalue')

        fragment_metadata_command = "bitbake-getvar -f {} --value {}"
        result = runCmd(fragment_metadata_command.format("selftest/test-fragment", "BB_CONF_FRAGMENT_SUMMARY"))
        self.assertIn("This is a configuration fragment intended for testing in oe-selftest context", result.output)
        result = runCmd(fragment_metadata_command.format("selftest/test-fragment", "BB_CONF_FRAGMENT_DESCRIPTION"))
        self.assertIn("It defines a variable that can be checked inside the test.", result.output)
        result = runCmd(fragment_metadata_command.format("selftest/more-fragments-here/test-another-fragment", "BB_CONF_FRAGMENT_SUMMARY"))
        self.assertIn("This is a second configuration fragment intended for testing in oe-selftest context", result.output)
        result = runCmd(fragment_metadata_command.format("selftest/more-fragments-here/test-another-fragment", "BB_CONF_FRAGMENT_DESCRIPTION"))
        self.assertIn("It defines another variable that can be checked inside the test.", result.output)

        runCmd('bitbake-config-build disable-fragment selftest/test-fragment')
        self.assertEqual(get_bb_var('SELFTEST_FRAGMENT_VARIABLE'), None)
        self.assertEqual(get_bb_var('SELFTEST_FRAGMENT_ANOTHER_VARIABLE'), 'someothervalue')

        runCmd('bitbake-config-build disable-fragment selftest/more-fragments-here/test-another-fragment')
        self.assertEqual(get_bb_var('SELFTEST_FRAGMENT_VARIABLE'), None)
        self.assertEqual(get_bb_var('SELFTEST_FRAGMENT_ANOTHER_VARIABLE'), None)

    def test_enable_disable_builtin_fragments(self):
        """
        Tests that the meta-selftest properly adds a new built-in fragment from
        its layer.conf configuration file.
        The test sequence goes as follows:
        1. Verify that SELFTEST_BUILTIN_FRAGMENT_VARIABLE is not set yet.
        2. Verify that SELFTEST_BUILTIN_FRAGMENT_VARIABLE is set after setting
           the fragment.
        3. Verify that SELFTEST_BUILTIN_FRAGMENT_VARIABLE is set after setting
           the fragment with another value that replaces the first one.
        4. Repeat steps 2 and 3 to verify that going back and forth between values
           works.
        5. Verify that SELFTEST_BUILTIN_FRAGMENT_VARIABLE is not set after
           removing the final assignment.
        """
        self.assertEqual(get_bb_var('SELFTEST_BUILTIN_FRAGMENT_VARIABLE'), None)

        runCmd('bitbake-config-build enable-fragment selftest-fragment/somevalue')
        self.assertEqual(get_bb_var('SELFTEST_BUILTIN_FRAGMENT_VARIABLE'), 'somevalue')

        runCmd('bitbake-config-build enable-fragment selftest-fragment/someothervalue')
        self.assertEqual(get_bb_var('SELFTEST_BUILTIN_FRAGMENT_VARIABLE'), 'someothervalue')

        runCmd('bitbake-config-build enable-fragment selftest-fragment/somevalue')
        self.assertEqual(get_bb_var('SELFTEST_BUILTIN_FRAGMENT_VARIABLE'), 'somevalue')

        runCmd('bitbake-config-build enable-fragment selftest-fragment/someothervalue')
        self.assertEqual(get_bb_var('SELFTEST_BUILTIN_FRAGMENT_VARIABLE'), 'someothervalue')

        runCmd('bitbake-config-build disable-fragment selftest-fragment/someothervalue')
        self.assertEqual(get_bb_var('SELFTEST_BUILTIN_FRAGMENT_VARIABLE'), None)

    def test_show_fragment(self):
        """
        Test that bitbake-config-build show-fragment returns the expected
        output. Use bitbake-config-build list-fragments --verbose to get the
        path to the fragment.
        """
        result = runCmd('bitbake-config-build --quiet list-fragments --verbose')
        test_fragment_re = re.compile(r'^Path: .*conf/fragments/test-fragment.conf$')
        fragment_path, fragment_content = '', ''

        for line in result.output.splitlines():
            m = re.match(test_fragment_re, line)
            if m:
                fragment_path = ' '.join(line.split()[1:])
                break

        if not fragment_path:
            raise Exception("Couldn't find the fragment")

        with open(fragment_path, 'r') as f:
            fragment_content = f'{fragment_path}:\n\n{f.read()}'.strip()

        result = runCmd('bitbake-config-build --quiet show-fragment selftest/test-fragment')

        self.assertEqual(result.output.strip(), fragment_content)
