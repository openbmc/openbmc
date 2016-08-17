from oeqa.selftest.base import oeSelfTest
from oeqa.utils.commands import bitbake, get_bb_var
from oeqa.utils.decorators import testcase
import glob
import os
import shutil


class Archiver(oeSelfTest):

    @testcase(1345)
    def test_archiver_allows_to_filter_on_recipe_name(self):
        """
        Summary:     The archiver should offer the possibility to filter on the recipe. (#6929)
        Expected:    1. Included recipe (busybox) should be included
                     2. Excluded recipe (zlib) should be excluded
        Product:     oe-core
        Author:      Daniel Istrate <daniel.alexandrux.istrate@intel.com>
        AutomatedBy: Daniel Istrate <daniel.alexandrux.istrate@intel.com>
        """

        include_recipe = 'busybox'
        exclude_recipe = 'zlib'

        features = 'INHERIT += "archiver"\n'
        features += 'ARCHIVER_MODE[src] = "original"\n'
        features += 'COPYLEFT_PN_INCLUDE = "%s"\n' % include_recipe
        features += 'COPYLEFT_PN_EXCLUDE = "%s"\n' % exclude_recipe

        # Update local.conf
        self.write_config(features)

        tmp_dir = get_bb_var('TMPDIR')
        deploy_dir_src = get_bb_var('DEPLOY_DIR_SRC')
        target_sys = get_bb_var('TARGET_SYS')
        src_path = os.path.join(deploy_dir_src, target_sys)

        # Delete tmp directory
        shutil.rmtree(tmp_dir)

        # Build core-image-minimal
        bitbake('core-image-minimal')

        # Check that include_recipe was included
        is_included = len(glob.glob(src_path + '/%s*' % include_recipe))
        self.assertEqual(1, is_included, 'Recipe %s was not included.' % include_recipe)

        # Check that exclude_recipe was excluded
        is_excluded = len(glob.glob(src_path + '/%s*' % exclude_recipe))
        self.assertEqual(0, is_excluded, 'Recipe %s was not excluded.' % exclude_recipe)
