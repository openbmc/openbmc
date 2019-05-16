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
RRECOMMENDS_${PN} = "a"
RRECOMMENDS_${PN}_libc = "b"
OVERRIDES = "libc:${PN}"
PN = "gtk+"
"""

    def test_parse_overrides(self):
        f = self.parsehelper(self.overridetest)
        d = bb.parse.handle(f.name, self.d)['']
        self.assertEqual(d.getVar("RRECOMMENDS"), "b")
        bb.data.expandKeys(d)
        self.assertEqual(d.getVar("RRECOMMENDS"), "b")
        d.setVar("RRECOMMENDS_gtk+", "c")
        self.assertEqual(d.getVar("RRECOMMENDS"), "c")

    overridetest2 = """
EXTRA_OECONF = ""
EXTRA_OECONF_class-target = "b"
EXTRA_OECONF_append = " c"
"""

    def test_parse_overrides(self):
        f = self.parsehelper(self.overridetest2)
        d = bb.parse.handle(f.name, self.d)['']
        d.appendVar("EXTRA_OECONF", " d")
        d.setVar("OVERRIDES", "class-target")
        self.assertEqual(d.getVar("EXTRA_OECONF"), "b c d")

    overridetest3 = """
DESCRIPTION = "A"
DESCRIPTION_${PN}-dev = "${DESCRIPTION} B"
PN = "bc"
"""

    def test_parse_combinations(self):
        f = self.parsehelper(self.overridetest3)
        d = bb.parse.handle(f.name, self.d)['']
        bb.data.expandKeys(d)
        self.assertEqual(d.getVar("DESCRIPTION_bc-dev"), "A B")
        d.setVar("DESCRIPTION", "E")
        d.setVar("DESCRIPTION_bc-dev", "C D")
        d.setVar("OVERRIDES", "bc-dev")
        self.assertEqual(d.getVar("DESCRIPTION"), "C D")


    classextend = """
VAR_var_override1 = "B"
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
addtask do_fetch do_patch

deltask do_fetch do_patch
"""
    def test_parse_addtask_deltask(self):
        import sys
        f = self.parsehelper(self.addtask_deltask)
        d = bb.parse.handle(f.name, self.d)['']

        stdout = sys.stdout.getvalue()
        self.assertTrue("addtask contained multiple 'before' keywords" in stdout)
        self.assertTrue("addtask contained multiple 'after' keywords" in stdout)
        self.assertTrue('addtask ignored: " do_patch"' in stdout)
        self.assertTrue('deltask ignored: " do_patch"' in stdout)
        #self.assertTrue('dependent task do_foo for do_patch does not exist' in stdout)

