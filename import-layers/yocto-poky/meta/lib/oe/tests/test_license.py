import unittest
import oe.license

class SeenVisitor(oe.license.LicenseVisitor):
    def __init__(self):
        self.seen = []
        oe.license.LicenseVisitor.__init__(self)

    def visit_Str(self, node):
        self.seen.append(node.s)

class TestSingleLicense(unittest.TestCase):
    licenses = [
        "GPLv2",
        "LGPL-2.0",
        "Artistic",
        "MIT",
        "GPLv3+",
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

class TestSimpleCombinations(unittest.TestCase):
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
        "(GPL-2.0|Proprietary)&BSD-4-clause&MIT": ["GPL-2.0", "BSD-4-clause", "MIT"],
    }
    preferred = ["BAR", "OMEGA", "BETA", "GPL-2.0"]
