from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import runCmd, bitbake, get_bb_var, get_bb_vars
from oeqa.utils.decorators import testcase
from oeqa.utils.ftools import write_file
from oeqa.core.decorator.oeid import OETestID

class Distrodata(OESelftestTestCase):

    @classmethod
    def setUpClass(cls):
        super(Distrodata, cls).setUpClass()
        feature = 'INHERIT += "distrodata"\n'
        feature += 'LICENSE_FLAGS_WHITELIST += " commercial"\n'

        cls.write_config(cls, feature)
        bitbake('-c checkpkg world')

    @OETestID(1902)
    def test_checkpkg(self):
        """
        Summary:     Test that upstream version checks do not regress
        Expected:    Upstream version checks should succeed except for the recipes listed in the exception list.
        Product:     oe-core
        Author:      Alexander Kanavin <alex.kanavin@gmail.com>
        """
        checkpkg_result = open(os.path.join(get_bb_var("LOG_DIR"), "checkpkg.csv")).readlines()[1:]
        regressed_failures = [pkg_data[0] for pkg_data in [pkg_line.split('\t') for pkg_line in checkpkg_result] if pkg_data[11] == 'UNKNOWN_BROKEN']
        regressed_successes = [pkg_data[0] for pkg_data in [pkg_line.split('\t') for pkg_line in checkpkg_result] if pkg_data[11] == 'KNOWN_BROKEN']
        msg = ""
        if len(regressed_failures) > 0:
            msg = msg + """
The following packages failed upstream version checks. Please fix them using UPSTREAM_CHECK_URI/UPSTREAM_CHECK_REGEX
(when using tarballs) or UPSTREAM_CHECK_GITTAGREGEX (when using git). If an upstream version check cannot be performed
(for example, if upstream does not use git tags), you can set UPSTREAM_VERSION_UNKNOWN to '1' in the recipe to acknowledge
that the check cannot be performed.
""" + "\n".join(regressed_failures)
        if len(regressed_successes) > 0:
            msg = msg + """
The following packages have been checked successfully for upstream versions,
but their recipes claim otherwise by setting UPSTREAM_VERSION_UNKNOWN. Please remove that line from the recipes.
""" + "\n".join(regressed_successes)
        self.assertTrue(len(regressed_failures) == 0 and len(regressed_successes) == 0, msg)

    def test_maintainers(self):
        """
        Summary:     Test that oe-core recipes have a maintainer
        Expected:    All oe-core recipes (except a few special static/testing ones) should have a maintainer listed in maintainers.inc file.
        Product:     oe-core
        Author:      Alexander Kanavin <alex.kanavin@gmail.com>
        """
        def is_exception(pkg):
            exceptions = ["packagegroup-", "initramfs-", "systemd-machine-units", "target-sdk-provides-dummy"]
            for i in exceptions:
                 if i in pkg:
                     return True
            return False

        def is_in_oe_core(recipe, recipes):
            self.assertTrue(recipe in recipes.keys(), "Recipe %s was not in 'bitbake-layers show-recipes' output" %(recipe))
            self.assertTrue(len(recipes[recipe]) > 0, "'bitbake-layers show-recipes' could not determine what layer(s) a recipe %s is in" %(recipe))
            try:
                recipes[recipe].index('meta')
                return True
            except ValueError:
                return False

        def get_recipe_layers():
            import re

            recipes = {}
            recipe_regex = re.compile('^(?P<name>.*):$')
            layer_regex = re.compile('^  (?P<name>\S*) +')
            output = runCmd('bitbake-layers show-recipes').output
            for line in output.split('\n'):
                recipe_name_obj = recipe_regex.search(line)
                if recipe_name_obj:
                    recipe_name = recipe_name_obj.group('name')
                    recipes[recipe_name] = []
                recipe_layer_obj = layer_regex.search(line)
                if recipe_layer_obj:
                    layer_name = recipe_layer_obj.group('name')
                    recipes[recipe_name].append(layer_name)
            return recipes

        checkpkg_result = open(os.path.join(get_bb_var("LOG_DIR"), "checkpkg.csv")).readlines()[1:]
        recipes_layers = get_recipe_layers()
        no_maintainer_list = [pkg_data[0] for pkg_data in [pkg_line.split('\t') for pkg_line in checkpkg_result] \
            if pkg_data[14] == '' and is_in_oe_core(pkg_data[0], recipes_layers) and not is_exception(pkg_data[0])]
        msg = """
The following packages do not have a maintainer assigned to them. Please add an entry to meta/conf/distro/include/maintainers.inc file.
""" + "\n".join(no_maintainer_list)
        self.assertTrue(len(no_maintainer_list) == 0, msg)

        with_maintainer_list = [pkg_data[0] for pkg_data in [pkg_line.split('\t') for pkg_line in checkpkg_result] \
            if pkg_data[14] != '' and is_in_oe_core(pkg_data[0], recipes_layers) and not is_exception(pkg_data[0])]
        msg = """
The list of oe-core packages with maintainers is empty. This may indicate that the test has regressed and needs fixing.
"""
        self.assertTrue(len(with_maintainer_list) > 0, msg)
