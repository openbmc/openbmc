# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
#
# BitBake Test for lib/bb/parse/
#
# Copyright (C) 2015 Richard Purdie
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License version 2 as
# published by the Free Software Foundation.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License along
# with this program; if not, write to the Free Software Foundation, Inc.,
# 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
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
        self.d = bb.data.init()
        bb.parse.siggen = bb.siggen.init(self.d)

    def parsehelper(self, content, suffix = ".bb"):

        f = tempfile.NamedTemporaryFile(suffix = suffix)
        f.write(content)
        f.flush()
        os.chdir(os.path.dirname(f.name))
        return f

    def test_parse_simple(self):
        f = self.parsehelper(self.testfile)
        d = bb.parse.handle(f.name, self.d)['']
        self.assertEqual(d.getVar("A", True), "1")
        self.assertEqual(d.getVar("B", True), "2")
        self.assertEqual(d.getVar("C", True), "3")

    def test_parse_incomplete_function(self):
        testfileB = self.testfile.replace("}", "")
        f = self.parsehelper(testfileB)
        with self.assertRaises(bb.parse.ParseError):
            d = bb.parse.handle(f.name, self.d)['']

    overridetest = """
RRECOMMENDS_${PN} = "a"
RRECOMMENDS_${PN}_libc = "b"
OVERRIDES = "libc:${PN}"
PN = "gtk+"
"""

    def test_parse_overrides(self):
        f = self.parsehelper(self.overridetest)
        d = bb.parse.handle(f.name, self.d)['']
        self.assertEqual(d.getVar("RRECOMMENDS", True), "b")
        bb.data.expandKeys(d)
        self.assertEqual(d.getVar("RRECOMMENDS", True), "b")
        d.setVar("RRECOMMENDS_gtk+", "c")
        self.assertEqual(d.getVar("RRECOMMENDS", True), "c")

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
        self.assertEqual(d.getVar("EXTRA_OECONF", True), "b c d")

    overridetest3 = """
DESCRIPTION = "A"
DESCRIPTION_${PN}-dev = "${DESCRIPTION} B"
PN = "bc"
"""

    def test_parse_combinations(self):
        f = self.parsehelper(self.overridetest3)
        d = bb.parse.handle(f.name, self.d)['']
        bb.data.expandKeys(d)
        self.assertEqual(d.getVar("DESCRIPTION_bc-dev", True), "A B")
        d.setVar("DESCRIPTION", "E")
        d.setVar("DESCRIPTION_bc-dev", "C D")
        d.setVar("OVERRIDES", "bc-dev")
        self.assertEqual(d.getVar("DESCRIPTION", True), "C D")


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
        self.assertEqual(d1.getVar("VAR_var", True), "B")
        self.assertEqual(d2.getVar("VAR_var", True), None)

