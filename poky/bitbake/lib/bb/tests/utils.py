#
# BitBake Tests for utils.py
#
# Copyright (C) 2012 Richard Purdie
#
# SPDX-License-Identifier: GPL-2.0-only
#

import unittest
import bb
import os
import tempfile
import re

class VerCmpString(unittest.TestCase):

    def test_vercmpstring(self):
        result = bb.utils.vercmp_string('1', '2')
        self.assertTrue(result < 0)
        result = bb.utils.vercmp_string('2', '1')
        self.assertTrue(result > 0)
        result = bb.utils.vercmp_string('1', '1.0')
        self.assertTrue(result < 0)
        result = bb.utils.vercmp_string('1', '1.1')
        self.assertTrue(result < 0)
        result = bb.utils.vercmp_string('1.1', '1_p2')
        self.assertTrue(result < 0)
        result = bb.utils.vercmp_string('1.0', '1.0+1.1-beta1')
        self.assertTrue(result < 0)
        result = bb.utils.vercmp_string('1.1', '1.0+1.1-beta1')
        self.assertTrue(result > 0)
        result = bb.utils.vercmp_string('1a', '1a1')
        self.assertTrue(result < 0)
        result = bb.utils.vercmp_string('1a1', '1a')
        self.assertTrue(result > 0)
        result = bb.utils.vercmp_string('1.', '1.1')
        self.assertTrue(result < 0)
        result = bb.utils.vercmp_string('1.1', '1.')
        self.assertTrue(result > 0)

    def test_explode_dep_versions(self):
        correctresult = {"foo" : ["= 1.10"]}
        result = bb.utils.explode_dep_versions2("foo (= 1.10)")
        self.assertEqual(result, correctresult)
        result = bb.utils.explode_dep_versions2("foo (=1.10)")
        self.assertEqual(result, correctresult)
        result = bb.utils.explode_dep_versions2("foo ( = 1.10)")
        self.assertEqual(result, correctresult)
        result = bb.utils.explode_dep_versions2("foo ( =1.10)")
        self.assertEqual(result, correctresult)
        result = bb.utils.explode_dep_versions2("foo ( = 1.10 )")
        self.assertEqual(result, correctresult)
        result = bb.utils.explode_dep_versions2("foo ( =1.10 )")
        self.assertEqual(result, correctresult)

    def test_vercmp_string_op(self):
        compareops = [('1', '1', '=', True),
                      ('1', '1', '==', True),
                      ('1', '1', '!=', False),
                      ('1', '1', '>', False),
                      ('1', '1', '<', False),
                      ('1', '1', '>=', True),
                      ('1', '1', '<=', True),
                      ('1', '0', '=', False),
                      ('1', '0', '==', False),
                      ('1', '0', '!=', True),
                      ('1', '0', '>', True),
                      ('1', '0', '<', False),
                      ('1', '0', '>>', True),
                      ('1', '0', '<<', False),
                      ('1', '0', '>=', True),
                      ('1', '0', '<=', False),
                      ('0', '1', '=', False),
                      ('0', '1', '==', False),
                      ('0', '1', '!=', True),
                      ('0', '1', '>', False),
                      ('0', '1', '<', True),
                      ('0', '1', '>>', False),
                      ('0', '1', '<<', True),
                      ('0', '1', '>=', False),
                      ('0', '1', '<=', True)]

        for arg1, arg2, op, correctresult in compareops:
            result = bb.utils.vercmp_string_op(arg1, arg2, op)
            self.assertEqual(result, correctresult, 'vercmp_string_op("%s", "%s", "%s") != %s' % (arg1, arg2, op, correctresult))

        # Check that clearly invalid operator raises an exception
        self.assertRaises(bb.utils.VersionStringException, bb.utils.vercmp_string_op, '0', '0', '$')


class Path(unittest.TestCase):
    def test_unsafe_delete_path(self):
        checkitems = [('/', True),
                      ('//', True),
                      ('///', True),
                      (os.getcwd().count(os.sep) * ('..' + os.sep), True),
                      (os.environ.get('HOME', '/home/test'), True),
                      ('/home/someone', True),
                      ('/home/other/', True),
                      ('/home/other/subdir', False),
                      ('', False)]
        for arg1, correctresult in checkitems:
            result = bb.utils._check_unsafe_delete_path(arg1)
            self.assertEqual(result, correctresult, '_check_unsafe_delete_path("%s") != %s' % (arg1, correctresult))

class Checksum(unittest.TestCase):
    filler = b"Shiver me timbers square-rigged spike Gold Road galleon bilge water boatswain wherry jack pirate. Mizzenmast rum lad Privateer jack salmagundi hang the jib piracy Pieces of Eight Corsair. Parrel marooned black spot yawl provost quarterdeck cable no prey, no pay spirits lateen sail."

    def test_md5(self):
        import hashlib
        with tempfile.NamedTemporaryFile() as f:
            f.write(self.filler)
            f.flush()
            checksum = bb.utils.md5_file(f.name)
            self.assertEqual(checksum, "bd572cd5de30a785f4efcb6eaf5089e3")

    def test_sha1(self):
        import hashlib
        with tempfile.NamedTemporaryFile() as f:
            f.write(self.filler)
            f.flush()
            checksum = bb.utils.sha1_file(f.name)
            self.assertEqual(checksum, "249eb8fd654732ea836d5e702d7aa567898eca71")

    def test_sha256(self):
        import hashlib
        with tempfile.NamedTemporaryFile() as f:
            f.write(self.filler)
            f.flush()
            checksum = bb.utils.sha256_file(f.name)
            self.assertEqual(checksum, "fcfbae8bf6b721dbb9d2dc6a9334a58f2031a9a9b302999243f99da4d7f12d0f")

class EditMetadataFile(unittest.TestCase):
    _origfile = """
# A comment
HELLO = "oldvalue"

THIS = "that"

# Another comment
NOCHANGE = "samevalue"
OTHER = 'anothervalue'

MULTILINE = "a1 \\
             a2 \\
             a3"

MULTILINE2 := " \\
               b1 \\
               b2 \\
               b3 \\
               "


MULTILINE3 = " \\
              c1 \\
              c2 \\
              c3 \\
"

do_functionname() {
    command1 ${VAL1} ${VAL2}
    command2 ${VAL3} ${VAL4}
}
"""
    def _testeditfile(self, varvalues, compareto, dummyvars=None):
        if dummyvars is None:
            dummyvars = []
        with tempfile.NamedTemporaryFile('w', delete=False) as tf:
            tf.write(self._origfile)
            tf.close()
            try:
                varcalls = []
                def handle_file(varname, origvalue, op, newlines):
                    self.assertIn(varname, varvalues, 'Callback called for variable %s not in the list!' % varname)
                    self.assertNotIn(varname, dummyvars, 'Callback called for variable %s in dummy list!' % varname)
                    varcalls.append(varname)
                    return varvalues[varname]

                bb.utils.edit_metadata_file(tf.name, varvalues.keys(), handle_file)
                with open(tf.name) as f:
                    modfile = f.readlines()
                # Ensure the output matches the expected output
                self.assertEqual(compareto.splitlines(True), modfile)
                # Ensure the callback function was called for every variable we asked for
                # (plus allow testing behaviour when a requested variable is not present)
                self.assertEqual(sorted(varvalues.keys()), sorted(varcalls + dummyvars))
            finally:
                os.remove(tf.name)


    def test_edit_metadata_file_nochange(self):
        # Test file doesn't get modified with nothing to do
        self._testeditfile({}, self._origfile)
        # Test file doesn't get modified with only dummy variables
        self._testeditfile({'DUMMY1': ('should_not_set', None, 0, True),
                        'DUMMY2': ('should_not_set_again', None, 0, True)}, self._origfile, dummyvars=['DUMMY1', 'DUMMY2'])
        # Test file doesn't get modified with some the same values
        self._testeditfile({'THIS': ('that', None, 0, True),
                        'OTHER': ('anothervalue', None, 0, True),
                        'MULTILINE3': ('               c1               c2               c3 ', None, 4, False)}, self._origfile)

    def test_edit_metadata_file_1(self):

        newfile1 = """
# A comment
HELLO = "newvalue"

THIS = "that"

# Another comment
NOCHANGE = "samevalue"
OTHER = 'anothervalue'

MULTILINE = "a1 \\
             a2 \\
             a3"

MULTILINE2 := " \\
               b1 \\
               b2 \\
               b3 \\
               "


MULTILINE3 = " \\
              c1 \\
              c2 \\
              c3 \\
"

do_functionname() {
    command1 ${VAL1} ${VAL2}
    command2 ${VAL3} ${VAL4}
}
"""
        self._testeditfile({'HELLO': ('newvalue', None, 4, True)}, newfile1)


    def test_edit_metadata_file_2(self):

        newfile2 = """
# A comment
HELLO = "oldvalue"

THIS = "that"

# Another comment
NOCHANGE = "samevalue"
OTHER = 'anothervalue'

MULTILINE = " \\
    d1 \\
    d2 \\
    d3 \\
    "

MULTILINE2 := " \\
               b1 \\
               b2 \\
               b3 \\
               "


MULTILINE3 = "nowsingle"

do_functionname() {
    command1 ${VAL1} ${VAL2}
    command2 ${VAL3} ${VAL4}
}
"""
        self._testeditfile({'MULTILINE': (['d1','d2','d3'], None, 4, False),
                        'MULTILINE3': ('nowsingle', None, 4, True),
                        'NOTPRESENT': (['a', 'b'], None, 4, False)}, newfile2, dummyvars=['NOTPRESENT'])


    def test_edit_metadata_file_3(self):

        newfile3 = """
# A comment
HELLO = "oldvalue"

# Another comment
NOCHANGE = "samevalue"
OTHER = "yetanothervalue"

MULTILINE = "e1 \\
             e2 \\
             e3 \\
             "

MULTILINE2 := "f1 \\
\tf2 \\
\t"


MULTILINE3 = " \\
              c1 \\
              c2 \\
              c3 \\
"

do_functionname() {
    othercommand_one a b c
    othercommand_two d e f
}
"""

        self._testeditfile({'do_functionname()': (['othercommand_one a b c', 'othercommand_two d e f'], None, 4, False),
                        'MULTILINE2': (['f1', 'f2'], None, '\t', True),
                        'MULTILINE': (['e1', 'e2', 'e3'], None, -1, True),
                        'THIS': (None, None, 0, False),
                        'OTHER': ('yetanothervalue', None, 0, True)}, newfile3)


    def test_edit_metadata_file_4(self):

        newfile4 = """
# A comment
HELLO = "oldvalue"

THIS = "that"

# Another comment
OTHER = 'anothervalue'

MULTILINE = "a1 \\
             a2 \\
             a3"

MULTILINE2 := " \\
               b1 \\
               b2 \\
               b3 \\
               "


"""

        self._testeditfile({'NOCHANGE': (None, None, 0, False),
                        'MULTILINE3': (None, None, 0, False),
                        'THIS': ('that', None, 0, False),
                        'do_functionname()': (None, None, 0, False)}, newfile4)


    def test_edit_metadata(self):
        newfile5 = """
# A comment
HELLO = "hithere"

# A new comment
THIS += "that"

# Another comment
NOCHANGE = "samevalue"
OTHER = 'anothervalue'

MULTILINE = "a1 \\
             a2 \\
             a3"

MULTILINE2 := " \\
               b1 \\
               b2 \\
               b3 \\
               "


MULTILINE3 = " \\
              c1 \\
              c2 \\
              c3 \\
"

NEWVAR = "value"

do_functionname() {
    command1 ${VAL1} ${VAL2}
    command2 ${VAL3} ${VAL4}
}
"""


        def handle_var(varname, origvalue, op, newlines):
            if varname == 'THIS':
                newlines.append('# A new comment\n')
            elif varname == 'do_functionname()':
                newlines.append('NEWVAR = "value"\n')
                newlines.append('\n')
            valueitem = varvalues.get(varname, None)
            if valueitem:
                return valueitem
            else:
                return (origvalue, op, 0, True)

        varvalues = {'HELLO': ('hithere', None, 0, True), 'THIS': ('that', '+=', 0, True)}
        varlist = ['HELLO', 'THIS', 'do_functionname()']
        (updated, newlines) = bb.utils.edit_metadata(self._origfile.splitlines(True), varlist, handle_var)
        self.assertTrue(updated, 'List should be updated but isn\'t')
        self.assertEqual(newlines, newfile5.splitlines(True))

    # Make sure the orig value matches what we expect it to be
    def test_edit_metadata_origvalue(self):
        origfile = """
MULTILINE = "  stuff \\
    morestuff"
"""
        expected_value = "stuff morestuff"
        global value_in_callback
        value_in_callback = ""

        def handle_var(varname, origvalue, op, newlines):
            global value_in_callback
            value_in_callback = origvalue
            return (origvalue, op, -1, False)

        bb.utils.edit_metadata(origfile.splitlines(True),
                               ['MULTILINE'],
                               handle_var)

        testvalue = re.sub('\s+', ' ', value_in_callback.strip())
        self.assertEqual(expected_value, testvalue)

class EditBbLayersConf(unittest.TestCase):

    def _test_bblayers_edit(self, before, after, add, remove, notadded, notremoved):
        with tempfile.NamedTemporaryFile('w', delete=False) as tf:
            tf.write(before)
            tf.close()
            try:
                actual_notadded, actual_notremoved = bb.utils.edit_bblayers_conf(tf.name, add, remove)
                with open(tf.name) as f:
                    actual_after = f.readlines()
                self.assertEqual(after.splitlines(True), actual_after)
                self.assertEqual(notadded, actual_notadded)
                self.assertEqual(notremoved, actual_notremoved)
            finally:
                os.remove(tf.name)


    def test_bblayers_remove(self):
        before = r"""
# A comment

BBPATH = "${TOPDIR}"
BBFILES ?= ""
BBLAYERS = " \
  /home/user/path/layer1 \
  /home/user/path/layer2 \
  /home/user/path/subpath/layer3 \
  /home/user/path/layer4 \
  "
"""
        after = r"""
# A comment

BBPATH = "${TOPDIR}"
BBFILES ?= ""
BBLAYERS = " \
  /home/user/path/layer1 \
  /home/user/path/subpath/layer3 \
  /home/user/path/layer4 \
  "
"""
        self._test_bblayers_edit(before, after,
                                 None,
                                 '/home/user/path/layer2',
                                 [],
                                 [])


    def test_bblayers_add(self):
        before = r"""
# A comment

BBPATH = "${TOPDIR}"
BBFILES ?= ""
BBLAYERS = " \
  /home/user/path/layer1 \
  /home/user/path/layer2 \
  /home/user/path/subpath/layer3 \
  /home/user/path/layer4 \
  "
"""
        after = r"""
# A comment

BBPATH = "${TOPDIR}"
BBFILES ?= ""
BBLAYERS = " \
  /home/user/path/layer1 \
  /home/user/path/layer2 \
  /home/user/path/subpath/layer3 \
  /home/user/path/layer4 \
  /other/path/to/layer5 \
  "
"""
        self._test_bblayers_edit(before, after,
                                 '/other/path/to/layer5/',
                                 None,
                                 [],
                                 [])


    def test_bblayers_add_remove(self):
        before = r"""
# A comment

BBPATH = "${TOPDIR}"
BBFILES ?= ""
BBLAYERS = " \
  /home/user/path/layer1 \
  /home/user/path/layer2 \
  /home/user/path/subpath/layer3 \
  /home/user/path/layer4 \
  "
"""
        after = r"""
# A comment

BBPATH = "${TOPDIR}"
BBFILES ?= ""
BBLAYERS = " \
  /home/user/path/layer1 \
  /home/user/path/layer2 \
  /home/user/path/layer4 \
  /other/path/to/layer5 \
  "
"""
        self._test_bblayers_edit(before, after,
                                 ['/other/path/to/layer5', '/home/user/path/layer2/'], '/home/user/path/subpath/layer3/',
                                 ['/home/user/path/layer2'],
                                 [])


    def test_bblayers_add_remove_home(self):
        before = r"""
# A comment

BBPATH = "${TOPDIR}"
BBFILES ?= ""
BBLAYERS = " \
  ~/path/layer1 \
  ~/path/layer2 \
  ~/otherpath/layer3 \
  ~/path/layer4 \
  "
"""
        after = r"""
# A comment

BBPATH = "${TOPDIR}"
BBFILES ?= ""
BBLAYERS = " \
  ~/path/layer2 \
  ~/path/layer4 \
  ~/path2/layer5 \
  "
"""
        self._test_bblayers_edit(before, after,
                                 [os.environ['HOME'] + '/path/layer4', '~/path2/layer5'],
                                 [os.environ['HOME'] + '/otherpath/layer3', '~/path/layer1', '~/path/notinlist'],
                                 [os.environ['HOME'] + '/path/layer4'],
                                 ['~/path/notinlist'])


    def test_bblayers_add_remove_plusequals(self):
        before = r"""
# A comment

BBPATH = "${TOPDIR}"
BBFILES ?= ""
BBLAYERS += " \
  /home/user/path/layer1 \
  /home/user/path/layer2 \
  "
"""
        after = r"""
# A comment

BBPATH = "${TOPDIR}"
BBFILES ?= ""
BBLAYERS += " \
  /home/user/path/layer2 \
  /home/user/path/layer3 \
  "
"""
        self._test_bblayers_edit(before, after,
                                 '/home/user/path/layer3',
                                 '/home/user/path/layer1',
                                 [],
                                 [])


    def test_bblayers_add_remove_plusequals2(self):
        before = r"""
# A comment

BBPATH = "${TOPDIR}"
BBFILES ?= ""
BBLAYERS += " \
  /home/user/path/layer1 \
  /home/user/path/layer2 \
  /home/user/path/layer3 \
  "
BBLAYERS += "/home/user/path/layer4"
BBLAYERS += "/home/user/path/layer5"
"""
        after = r"""
# A comment

BBPATH = "${TOPDIR}"
BBFILES ?= ""
BBLAYERS += " \
  /home/user/path/layer2 \
  /home/user/path/layer3 \
  "
BBLAYERS += "/home/user/path/layer5"
BBLAYERS += "/home/user/otherpath/layer6"
"""
        self._test_bblayers_edit(before, after,
                                 ['/home/user/otherpath/layer6', '/home/user/path/layer3'], ['/home/user/path/layer1', '/home/user/path/layer4', '/home/user/path/layer7'],
                                 ['/home/user/path/layer3'],
                                 ['/home/user/path/layer7'])


class GetReferencedVars(unittest.TestCase):
    def setUp(self):
        self.d = bb.data.init()

    def check_referenced(self, expression, expected_layers):
        vars = bb.utils.get_referenced_vars(expression, self.d)

        # Do the easy check first - is every variable accounted for?
        expected_vars = set.union(set(), *expected_layers)
        got_vars = set(vars)
        self.assertSetEqual(got_vars, expected_vars)

        # Now test the order of the layers
        start = 0
        for i, expected_layer in enumerate(expected_layers):
            got_layer = set(vars[start:len(expected_layer)+start])
            start += len(expected_layer)
            self.assertSetEqual(got_layer, expected_layer)

    def test_no_vars(self):
        self.check_referenced("", [])
        self.check_referenced(" ", [])
        self.check_referenced(" no vars here! ", [])

    def test_single_layer(self):
        self.check_referenced("${VAR}", [{"VAR"}])
        self.check_referenced("${VAR} ${VAR}", [{"VAR"}])

    def test_two_layer(self):
        self.d.setVar("VAR", "${B}")
        self.check_referenced("${VAR}", [{"VAR"}, {"B"}])
        self.check_referenced("${@d.getVar('VAR')}", [{"VAR"}, {"B"}])

    def test_more_complicated(self):
        self.d["SRC_URI"] = "${QT_GIT}/${QT_MODULE}.git;name=${QT_MODULE};${QT_MODULE_BRANCH_PARAM};protocol=${QT_GIT_PROTOCOL}"
        self.d["QT_GIT"] = "git://code.qt.io/${QT_GIT_PROJECT}"
        self.d["QT_MODULE_BRANCH_PARAM"] = "branch=${QT_MODULE_BRANCH}"
        self.d["QT_MODULE"] = "${BPN}"
        self.d["BPN"] = "something to do with ${PN} and ${SPECIAL_PKGSUFFIX}"

        layers = [{"SRC_URI"}, {"QT_GIT", "QT_MODULE", "QT_MODULE_BRANCH_PARAM", "QT_GIT_PROTOCOL"}, {"QT_GIT_PROJECT", "QT_MODULE_BRANCH", "BPN"}, {"PN", "SPECIAL_PKGSUFFIX"}]
        self.check_referenced("${SRC_URI}", layers)
