#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

import os
import shutil
import tempfile

from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import get_bb_var, runCmd

class ExternalSrc(OESelftestTestCase):
    # test that srctree_hash_files does not crash
    # we should be actually checking do_compile[file-checksums] but oeqa currently does not support it
    #     so we check only that a recipe with externalsrc can be parsed
    def test_externalsrc_srctree_hash_files(self):
        test_recipe = "git-submodule-test"
        git_url = "git://git.yoctoproject.org/git-submodule-test"
        externalsrc_dir = tempfile.TemporaryDirectory(prefix="externalsrc").name

        self.write_config(
            """
INHERIT += "externalsrc"
EXTERNALSRC:pn-%s = "%s"
""" % (test_recipe, externalsrc_dir)
        )

        # test with git without submodules
        runCmd('git clone %s %s' % (git_url, externalsrc_dir))
        os.unlink(externalsrc_dir + "/.gitmodules")
        open(".gitmodules", 'w').close()  # local file .gitmodules in cwd should not affect externalsrc parsing
        self.assertEqual(get_bb_var("S", test_recipe), externalsrc_dir, msg = "S does not equal to EXTERNALSRC")
        os.unlink(".gitmodules")

        # test with git with submodules
        runCmd('git checkout .gitmodules', cwd=externalsrc_dir)
        runCmd('git submodule update --init --recursive', cwd=externalsrc_dir)
        self.assertEqual(get_bb_var("S", test_recipe), externalsrc_dir, msg = "S does not equal to EXTERNALSRC")

        # test without git
        shutil.rmtree(os.path.join(externalsrc_dir, ".git"))
        self.assertEqual(get_bb_var("S", test_recipe), externalsrc_dir, msg = "S does not equal to EXTERNALSRC")
