#
# BitBake Test for lib/bb/parse/
#
# Copyright (C) 2015 Richard Purdie
#
# SPDX-License-Identifier: GPL-2.0-only
#

import unittest
import tempfile
import logging
import bb
import os

logger = logging.getLogger('BitBake.TestParse')

import bb.parse
import bb.data
import bb.siggen

class ParseTest(unittest.TestCase):

    testfile = """
A = "1"
B = "2"
do_install() {
	echo "hello"
}

C = "3"
"""

    def setUp(self):
        self.origdir = os.getcwd()
        self.d = bb.data.init()
        bb.parse.siggen = bb.siggen.init(self.d)

    def tearDown(self):
        os.chdir(self.origdir)

    def parsehelper(self, content, suffix = ".bb"):

        f = tempfile.NamedTemporaryFile(suffix = suffix)
        f.write(bytes(content, "utf-8"))
        f.flush()
        os.chdir(os.path.dirname(f.name))
        return f

    def test_parse_simple(self):
        f = self.parsehelper(self.testfile)
        d = bb.parse.handle(f.name, self.d)['']
        self.assertEqual(d.getVar("A"), "1")
        self.assertEqual(d.getVar("B"), "2")
        self.assertEqual(d.getVar("C"), "3")

    def test_parse_incomplete_function(self):
        testfileB = self.testfile.replace("}", "")
        f = self.parsehelper(testfileB)
        with self.assertRaises(bb.parse.ParseError):
            d = bb.parse.handle(f.name, self.d)['']

    unsettest = """
A = "1"
B = "2"
B[flag] = "3"

unset A
unset B[flag]
"""

    def test_parse_unset(self):
        f = self.parsehelper(self.unsettest)
        d = bb.parse.handle(f.name, self.d)['']
        self.assertEqual(d.getVar("A"), None)
        self.assertEqual(d.getVarFlag("A","flag"), None)
        self.assertEqual(d.getVar("B"), "2")

    defaulttest = """
A = "set value"
A ??= "default value"

A[flag_set_vs_question] = "set flag"
A[flag_set_vs_question] ?= "question flag"

A[flag_set_vs_default] = "set flag"
A[flag_set_vs_default] ??= "default flag"

A[flag_question] ?= "question flag"

A[flag_default] ??= "default flag"

A[flag_question_vs_default] ?= "question flag"
A[flag_question_vs_default] ??= "default flag"

A[flag_default_vs_question] ??= "default flag"
A[flag_default_vs_question] ?= "question flag"

A[flag_set_question_default] = "set flag"
A[flag_set_question_default] ?= "question flag"
A[flag_set_question_default] ??= "default flag"

A[flag_set_default_question] = "set flag"
A[flag_set_default_question] ??= "default flag"
A[flag_set_default_question] ?= "question flag"

A[flag_set_twice] = "set flag first"
A[flag_set_twice] = "set flag second"

A[flag_question_twice] ?= "question flag first"
A[flag_question_twice] ?= "question flag second"

A[flag_default_twice] ??= "default flag first"
A[flag_default_twice] ??= "default flag second"
"""
    def test_parse_defaulttest(self):
        f = self.parsehelper(self.defaulttest)
        d = bb.parse.handle(f.name, self.d)['']
        self.assertEqual(d.getVar("A"), "set value")
        self.assertEqual(d.getVarFlag("A","flag_set_vs_question"), "set flag")
        self.assertEqual(d.getVarFlag("A","flag_set_vs_default"), "set flag")
        self.assertEqual(d.getVarFlag("A","flag_question"), "question flag")
        self.assertEqual(d.getVarFlag("A","flag_default"), "default flag")
        self.assertEqual(d.getVarFlag("A","flag_question_vs_default"), "question flag")
        self.assertEqual(d.getVarFlag("A","flag_default_vs_question"), "question flag")
        self.assertEqual(d.getVarFlag("A","flag_set_question_default"), "set flag")
        self.assertEqual(d.getVarFlag("A","flag_set_default_question"), "set flag")
        self.assertEqual(d.getVarFlag("A","flag_set_twice"), "set flag second")
        self.assertEqual(d.getVarFlag("A","flag_question_twice"), "question flag first")
        self.assertEqual(d.getVarFlag("A","flag_default_twice"), "default flag second")

    exporttest = """
A = "a"
export B = "b"
export C
exportD = "d"
"""

    def test_parse_exports(self):
        f = self.parsehelper(self.exporttest)
        d = bb.parse.handle(f.name, self.d)['']
        self.assertEqual(d.getVar("A"), "a")
        self.assertIsNone(d.getVarFlag("A", "export"))
        self.assertEqual(d.getVar("B"), "b")
        self.assertEqual(d.getVarFlag("B", "export"), 1)
        self.assertIsNone(d.getVar("C"))
        self.assertEqual(d.getVarFlag("C", "export"), 1)
        self.assertIsNone(d.getVar("D"))
        self.assertIsNone(d.getVarFlag("D", "export"))
        self.assertEqual(d.getVar("exportD"), "d")
        self.assertIsNone(d.getVarFlag("exportD", "export"))


    overridetest = """
RRECOMMENDS:${PN} = "a"
RRECOMMENDS:${PN}:libc = "b"
OVERRIDES = "libc:${PN}"
PN = "gtk+"
"""

    def test_parse_overrides(self):
        f = self.parsehelper(self.overridetest)
        d = bb.parse.handle(f.name, self.d)['']
        self.assertEqual(d.getVar("RRECOMMENDS"), "b")
        bb.data.expandKeys(d)
        self.assertEqual(d.getVar("RRECOMMENDS"), "b")
        d.setVar("RRECOMMENDS:gtk+", "c")
        self.assertEqual(d.getVar("RRECOMMENDS"), "c")

    overridetest2 = """
EXTRA_OECONF = ""
EXTRA_OECONF:class-target = "b"
EXTRA_OECONF:append = " c"
"""

    def test_parse_overrides2(self):
        f = self.parsehelper(self.overridetest2)
        d = bb.parse.handle(f.name, self.d)['']
        d.appendVar("EXTRA_OECONF", " d")
        d.setVar("OVERRIDES", "class-target")
        self.assertEqual(d.getVar("EXTRA_OECONF"), "b c d")

    overridetest3 = """
DESCRIPTION = "A"
DESCRIPTION:${PN}-dev = "${DESCRIPTION} B"
PN = "bc"
"""

    def test_parse_combinations(self):
        f = self.parsehelper(self.overridetest3)
        d = bb.parse.handle(f.name, self.d)['']
        bb.data.expandKeys(d)
        self.assertEqual(d.getVar("DESCRIPTION:bc-dev"), "A B")
        d.setVar("DESCRIPTION", "E")
        d.setVar("DESCRIPTION:bc-dev", "C D")
        d.setVar("OVERRIDES", "bc-dev")
        self.assertEqual(d.getVar("DESCRIPTION"), "C D")


    classextend = """
VAR_var:override1 = "B"
EXTRA = ":override1"
OVERRIDES = "nothing${EXTRA}"

BBCLASSEXTEND = "###CLASS###"
"""
    classextend_bbclass = """
EXTRA = ""
python () {
    d.renameVar("VAR_var", "VAR_var2")
}
"""

    #
    # Test based upon a real world data corruption issue. One
    # data store changing a variable poked through into a different data
    # store. This test case replicates that issue where the value 'B' would 
    # become unset/disappear.
    #
    def test_parse_classextend_contamination(self):
        self.d.setVar("__bbclasstype", "recipe")
        cls = self.parsehelper(self.classextend_bbclass, suffix=".bbclass")
        #clsname = os.path.basename(cls.name).replace(".bbclass", "")
        self.classextend = self.classextend.replace("###CLASS###", cls.name)
        f = self.parsehelper(self.classextend)
        alldata = bb.parse.handle(f.name, self.d)
        d1 = alldata['']
        d2 = alldata[cls.name]
        self.assertEqual(d1.getVar("VAR_var"), "B")
        self.assertEqual(d2.getVar("VAR_var"), None)

    addtask_deltask = """
addtask do_patch after do_foo after do_unpack before do_configure before do_compile
addtask do_fetch2 do_patch2

addtask do_myplaintask
addtask do_myplaintask2
deltask do_myplaintask2
addtask do_mytask# comment
addtask do_mytask2 # comment2
addtask do_mytask3
deltask do_mytask3# comment
deltask do_mytask4 # comment2

# Ensure a missing task prefix on after works
addtask do_mytask5 after mytask

MYVAR = "do_patch"
EMPTYVAR = ""
deltask do_fetch ${MYVAR} ${EMPTYVAR}
deltask ${EMPTYVAR}
"""
    def test_parse_addtask_deltask(self):

        f = self.parsehelper(self.addtask_deltask)
        d = bb.parse.handle(f.name, self.d)['']

        self.assertSequenceEqual(['do_fetch2', 'do_patch2', 'do_myplaintask', 'do_mytask', 'do_mytask2', 'do_mytask5'], bb.build.listtasks(d))
        self.assertEqual(['do_mytask'], d.getVarFlag("do_mytask5", "deps"))

    broken_multiline_comment = """
# First line of comment \\
# Second line of comment \\

"""
    def test_parse_broken_multiline_comment(self):
        f = self.parsehelper(self.broken_multiline_comment)
        with self.assertRaises(bb.BBHandledException):
            d = bb.parse.handle(f.name, self.d)['']


    comment_in_var = """
VAR = " \\
    SOMEVAL \\
#   some comment \\
    SOMEOTHERVAL \\
"
"""
    def test_parse_comment_in_var(self):
        f = self.parsehelper(self.comment_in_var)
        with self.assertRaises(bb.BBHandledException):
            d = bb.parse.handle(f.name, self.d)['']


    at_sign_in_var_flag = """
A[flag@.service] = "nonet"
B[flag@.target] = "ntb"
C[f] = "flag"

unset A[flag@.service]
"""
    def test_parse_at_sign_in_var_flag(self):
        f = self.parsehelper(self.at_sign_in_var_flag)
        d = bb.parse.handle(f.name, self.d)['']
        self.assertEqual(d.getVar("A"), None)
        self.assertEqual(d.getVar("B"), None)
        self.assertEqual(d.getVarFlag("A","flag@.service"), None)
        self.assertEqual(d.getVarFlag("B","flag@.target"), "ntb")
        self.assertEqual(d.getVarFlag("C","f"), "flag")

    def test_parse_invalid_at_sign_in_var_flag(self):
        invalid_at_sign = self.at_sign_in_var_flag.replace("B[f", "B[@f")
        f = self.parsehelper(invalid_at_sign)
        with self.assertRaises(bb.parse.ParseError):
            d = bb.parse.handle(f.name, self.d)['']

    export_function_recipe = """
inherit someclass
"""

    export_function_recipe2 = """
inherit someclass

do_compile () {
    false
}

python do_compilepython () {
    bb.note("Something else")
}

"""
    export_function_class = """
someclass_do_compile() {
    true
}

python someclass_do_compilepython () {
    bb.note("Something")
}

EXPORT_FUNCTIONS do_compile do_compilepython
"""

    export_function_class2 = """
secondclass_do_compile() {
    true
}

python secondclass_do_compilepython () {
    bb.note("Something")
}

EXPORT_FUNCTIONS do_compile do_compilepython
"""

    def test_parse_export_functions(self):
        def check_function_flags(d):
            self.assertEqual(d.getVarFlag("do_compile", "func"), 1)
            self.assertEqual(d.getVarFlag("do_compilepython", "func"), 1)
            self.assertEqual(d.getVarFlag("do_compile", "python"), None)
            self.assertEqual(d.getVarFlag("do_compilepython", "python"), "1")

        with tempfile.TemporaryDirectory() as tempdir:
            self.d.setVar("__bbclasstype", "recipe")
            recipename = tempdir + "/recipe.bb"
            os.makedirs(tempdir + "/classes")
            with open(tempdir + "/classes/someclass.bbclass", "w") as f:
                f.write(self.export_function_class)
                f.flush()
            with open(tempdir + "/classes/secondclass.bbclass", "w") as f:
                f.write(self.export_function_class2)
                f.flush()

            with open(recipename, "w") as f:
                f.write(self.export_function_recipe)
                f.flush()
            os.chdir(tempdir)
            d = bb.parse.handle(recipename, bb.data.createCopy(self.d))['']
            self.assertIn("someclass_do_compile", d.getVar("do_compile"))
            self.assertIn("someclass_do_compilepython", d.getVar("do_compilepython"))
            check_function_flags(d)

            recipename2 = tempdir + "/recipe2.bb"
            with open(recipename2, "w") as f:
                f.write(self.export_function_recipe2)
                f.flush()

            d = bb.parse.handle(recipename2, bb.data.createCopy(self.d))['']
            self.assertNotIn("someclass_do_compile", d.getVar("do_compile"))
            self.assertNotIn("someclass_do_compilepython", d.getVar("do_compilepython"))
            self.assertIn("false", d.getVar("do_compile"))
            self.assertIn("else", d.getVar("do_compilepython"))
            check_function_flags(d)

            with open(recipename, "a+") as f:
                f.write("\ninherit secondclass\n")
                f.flush()
            with open(recipename2, "a+") as f:
                f.write("\ninherit secondclass\n")
                f.flush()

            d = bb.parse.handle(recipename, bb.data.createCopy(self.d))['']
            self.assertIn("secondclass_do_compile", d.getVar("do_compile"))
            self.assertIn("secondclass_do_compilepython", d.getVar("do_compilepython"))
            check_function_flags(d)

            d = bb.parse.handle(recipename2, bb.data.createCopy(self.d))['']
            self.assertNotIn("someclass_do_compile", d.getVar("do_compile"))
            self.assertNotIn("someclass_do_compilepython", d.getVar("do_compilepython"))
            self.assertIn("false", d.getVar("do_compile"))
            self.assertIn("else", d.getVar("do_compilepython"))
            check_function_flags(d)

    export_function_unclosed_tab = """
do_compile () {
       bb.note("Something")
\t}
"""
    export_function_unclosed_space = """
do_compile () {
       bb.note("Something")
 }
"""
    export_function_residue = """
do_compile () {
       bb.note("Something")
}

include \\
"""

    def test_unclosed_functions(self):
        def test_helper(content, expected_error):
            with tempfile.TemporaryDirectory() as tempdir:
                recipename = tempdir + "/recipe_unclosed.bb"
                with open(recipename, "w") as f:
                    f.write(content)
                    f.flush()
                os.chdir(tempdir)
                with self.assertRaises(bb.parse.ParseError) as error:
                    bb.parse.handle(recipename, bb.data.createCopy(self.d))
                self.assertIn(expected_error, str(error.exception))

        with tempfile.TemporaryDirectory() as tempdir:
            test_helper(self.export_function_unclosed_tab, "Unparsed lines from unclosed function")
            test_helper(self.export_function_unclosed_space, "Unparsed lines from unclosed function")
            test_helper(self.export_function_residue, "Unparsed lines")

            recipename_closed = tempdir + "/recipe_closed.bb"
            with open(recipename_closed, "w") as in_file:
                lines = self.export_function_unclosed_tab.split("\n")
                lines[3] = "}"
                in_file.write("\n".join(lines))
                in_file.flush()
            bb.parse.handle(recipename_closed, bb.data.createCopy(self.d))

    special_character_assignment = """
A+="a"
A+ = "b"
+ = "c"
"""
    ambigous_assignment = """
+= "d"
"""
    def test_parse_special_character_assignment(self):
        f = self.parsehelper(self.special_character_assignment)
        d = bb.parse.handle(f.name, self.d)['']
        self.assertEqual(d.getVar("A"), " a")
        self.assertEqual(d.getVar("A+"), "b")
        self.assertEqual(d.getVar("+"), "c")

        f = self.parsehelper(self.ambigous_assignment)
        with self.assertRaises(bb.parse.ParseError) as error:
            bb.parse.handle(f.name, self.d)
        self.assertIn("Empty variable name in assignment", str(error.exception))
