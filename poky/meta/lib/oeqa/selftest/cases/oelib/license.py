#
# SPDX-License-Identifier: MIT
#

from unittest.case import TestCase
import oe.license

class SeenVisitor(oe.license.LicenseVisitor):
    def __init__(self):
        self.seen = []
        oe.license.LicenseVisitor.__init__(self)

    def visit_Str(self, node):
        self.seen.append(node.s)

class TestSingleLicense(TestCase):
    licenses = [
        "GPL-2.0-only",
        "LGPL-2.0-only",
        "Artistic-1.0",
        "MIT",
        "GPL-3.0-or-later",
        "FOO_BAR",
    ]
    invalid_licenses = ["GPL/BSD"]

    @staticmethod
    def parse(licensestr):
        visitor = SeenVisitor()
        visitor.visit_string(licensestr)
        return visitor.seen

    def test_single_licenses(self):
        for license in self.licenses:
            licenses = self.parse(license)
            self.assertListEqual(licenses, [license])

    def test_invalid_licenses(self):
        for license in self.invalid_licenses:
            with self.assertRaises(oe.license.InvalidLicense) as cm:
                self.parse(license)
            self.assertEqual(cm.exception.license, license)

class TestSimpleCombinations(TestCase):
    tests = {
        "FOO&BAR": ["FOO", "BAR"],
        "BAZ & MOO": ["BAZ", "MOO"],
        "ALPHA|BETA": ["ALPHA"],
        "BAZ&MOO|FOO": ["FOO"],
        "FOO&BAR|BAZ": ["FOO", "BAR"],
    }
    preferred = ["ALPHA", "FOO", "BAR"]

    def test_tests(self):
        def choose(a, b):
            if all(lic in self.preferred for lic in b):
                return b
            else:
                return a

        for license, expected in self.tests.items():
            licenses = oe.license.flattened_licenses(license, choose)
            self.assertListEqual(licenses, expected)

class TestComplexCombinations(TestSimpleCombinations):
    tests = {
        "FOO & (BAR | BAZ)&MOO": ["FOO", "BAR", "MOO"],
        "(ALPHA|(BETA&THETA)|OMEGA)&DELTA": ["OMEGA", "DELTA"],
        "((ALPHA|BETA)&FOO)|BAZ": ["BETA", "FOO"],
        "(GPL-2.0-only|Proprietary)&BSD-4-clause&MIT": ["GPL-2.0-only", "BSD-4-clause", "MIT"],
    }
    preferred = ["BAR", "OMEGA", "BETA", "GPL-2.0-only"]

class TestIsIncluded(TestCase):
    tests = {
        ("FOO | BAR", None, None):
            [True, ["FOO"]],
        ("FOO | BAR", None, "FOO"):
            [True, ["BAR"]],
        ("FOO | BAR", "BAR", None):
            [True, ["BAR"]],
        ("FOO | BAR & FOOBAR", "*BAR", None):
            [True, ["BAR", "FOOBAR"]],
        ("FOO | BAR & FOOBAR", None, "FOO*"):
            [False, ["FOOBAR"]],
        ("(FOO | BAR) & FOOBAR | BARFOO", None, "FOO"):
            [True, ["BAR", "FOOBAR"]],
        ("(FOO | BAR) & FOOBAR | BAZ & MOO & BARFOO", None, "FOO"):
            [True, ["BAZ", "MOO", "BARFOO"]],
        ("GPL-3.0-or-later & GPL-2.0-only & LGPL-2.1-only | Proprietary", None, None):
            [True, ["GPL-3.0-or-later", "GPL-2.0-only", "LGPL-2.1-only"]],
        ("GPL-3.0-or-later & GPL-2.0-only & LGPL-2.1-only | Proprietary", None, "GPL-3.0-or-later"):
            [True, ["Proprietary"]],
        ("GPL-3.0-or-later & GPL-2.0-only & LGPL-2.1-only | Proprietary", None, "GPL-3.0-or-later Proprietary"):
            [False, ["GPL-3.0-or-later"]]
    }

    def test_tests(self):
        for args, expected in self.tests.items():
            is_included, licenses = oe.license.is_included(
                args[0], (args[1] or '').split(), (args[2] or '').split())
            self.assertEqual(is_included, expected[0])
            self.assertListEqual(licenses, expected[1])
