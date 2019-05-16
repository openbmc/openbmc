#
# SPDX-License-Identifier: MIT
#

import os
import re
import time
import logging
import bb.tinfoil

from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import runCmd, get_test_layer


def setUpModule():
    global tinfoil
    global metaselftestpath
    metaselftestpath = get_test_layer()
    tinfoil = bb.tinfoil.Tinfoil(tracking=True)
    tinfoil.prepare(config_only=False, quiet=2)


def tearDownModule():
    tinfoil.shutdown()


class RecipeUtilsTests(OESelftestTestCase):
    """ Tests for the recipeutils module functions """

    def test_patch_recipe_varflag(self):
        import oe.recipeutils
        rd = tinfoil.parse_recipe('python3-async-test')
        vals = {'SRC_URI[md5sum]': 'aaaaaa', 'LICENSE': 'something'}
        patches = oe.recipeutils.patch_recipe(rd, rd.getVar('FILE'), vals, patch=True, relpath=metaselftestpath)

        expected_patch = """
--- a/recipes-devtools/python/python-async-test.inc
+++ b/recipes-devtools/python/python-async-test.inc
@@ -1,14 +1,14 @@
 SUMMARY = "Python framework to process interdependent tasks in a pool of workers"
 HOMEPAGE = "http://github.com/gitpython-developers/async"
 SECTION = "devel/python"
-LICENSE = "BSD"
+LICENSE = "something"
 LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=88df8e78b9edfd744953862179f2d14e"
 
 inherit pypi
 
 PYPI_PACKAGE = "async"
 
-SRC_URI[md5sum] = "9b06b5997de2154f3bc0273f80bcef6b"
+SRC_URI[md5sum] = "aaaaaa"
 SRC_URI[sha256sum] = "ac6894d876e45878faae493b0cf61d0e28ec417334448ac0a6ea2229d8343051"
 
 RDEPENDS_${PN} += "${PYTHON_PN}-threading"
"""
        patchlines = []
        for f in patches:
            for line in f:
                patchlines.append(line)
        self.maxDiff = None
        self.assertEqual(''.join(patchlines).strip(), expected_patch.strip())


    def test_patch_recipe_singleappend(self):
        import oe.recipeutils
        rd = tinfoil.parse_recipe('recipeutils-test')
        val = rd.getVar('SRC_URI', False).split()
        del val[1]
        val = ' '.join(val)
        vals = {'SRC_URI': val}
        patches = oe.recipeutils.patch_recipe(rd, rd.getVar('FILE'), vals, patch=True, relpath=metaselftestpath)

        expected_patch = """
--- a/recipes-test/recipeutils/recipeutils-test_1.2.bb
+++ b/recipes-test/recipeutils/recipeutils-test_1.2.bb
@@ -8,6 +8,4 @@
 
 BBCLASSEXTEND = "native nativesdk"
 
-SRC_URI += "file://somefile"
-
 SRC_URI_append = " file://anotherfile"
"""
        patchlines = []
        for f in patches:
            for line in f:
                patchlines.append(line)
        self.assertEqual(''.join(patchlines).strip(), expected_patch.strip())


    def test_patch_recipe_appends(self):
        import oe.recipeutils
        rd = tinfoil.parse_recipe('recipeutils-test')
        val = rd.getVar('SRC_URI', False).split()
        vals = {'SRC_URI': val[0]}
        patches = oe.recipeutils.patch_recipe(rd, rd.getVar('FILE'), vals, patch=True, relpath=metaselftestpath)

        expected_patch = """
--- a/recipes-test/recipeutils/recipeutils-test_1.2.bb
+++ b/recipes-test/recipeutils/recipeutils-test_1.2.bb
@@ -8,6 +8,3 @@
 
 BBCLASSEXTEND = "native nativesdk"
 
-SRC_URI += "file://somefile"
-
-SRC_URI_append = " file://anotherfile"
"""
        patchlines = []
        for f in patches:
            for line in f:
                patchlines.append(line)
        self.assertEqual(''.join(patchlines).strip(), expected_patch.strip())


    def test_validate_pn(self):
        import oe.recipeutils
        expected_results = {
            'test': '',
            'glib-2.0': '',
            'gtk+': '',
            'forcevariable': 'reserved',
            'pn-something': 'reserved',
            'test.bb': 'file',
            'test_one': 'character',
            'test!': 'character',
        }

        for pn, expected in expected_results.items():
            result = oe.recipeutils.validate_pn(pn)
            if expected:
                self.assertIn(expected, result)
            else:
                self.assertEqual(result, '')

    def test_split_var_value(self):
        import oe.recipeutils
        res = oe.recipeutils.split_var_value('test.1 test.2 ${@call_function("hi there world", false)} test.4')
        self.assertEqual(res, ['test.1', 'test.2', '${@call_function("hi there world", false)}', 'test.4'])
