#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

from unittest.case import TestCase
import oe.license
import oe.spdx_license


class TestSingleLicense(TestCase):
    licenses = [
        "GPL-2.0-only",
        "LGPL-2.0-only",
        "Artistic-1.0",
        "MIT",
        "GPL-3.0-or-later",
        "LicenseRef-FOO-BAR",
    ]
    invalid_licenses = ["GPL/BSD"]

    @staticmethod
    def parse(licensestr):
        return oe.spdx_license.parse(licensestr)

    def test_single_licenses(self):
        for license in self.licenses:
            n = oe.spdx_license.parse(license)
            self.assertListEqual([n.ident], [license])

    def test_invalid_licenses(self):
        for license in self.invalid_licenses:
            with self.assertRaises(oe.spdx_license.ParseError) as cm:
                oe.spdx_license.parse(license)
            self.assertEqual(cm.exception.expression, license)


class TestSimpleCombinations(TestCase):
    tests = [
        (
            "LicenseRef-FOO AND LicenseRef-BAR",
            ["LicenseRef-FOO", "LicenseRef-BAR"],
        ),
        (
            "LicenseRef-BAZ AND LicenseRef-MOO",
            ["LicenseRef-BAZ", "LicenseRef-MOO"],
        ),
        (
            "LicenseRef-ALPHA OR LicenseRef-BETA",
            ["LicenseRef-ALPHA"],
        ),
        (
            "LicenseRef-BAZ AND LicenseRef-MOO OR LicenseRef-FOO",
            ["LicenseRef-FOO"],
        ),
        (
            "LicenseRef-FOO AND LicenseRef-BAR OR LicenseRef-BAZ",
            ["LicenseRef-FOO", "LicenseRef-BAR"],
        ),
    ]
    preferred = ["LicenseRef-ALPHA", "LicenseRef-FOO", "LicenseRef-BAR"]

    def test_tests(self):
        def choose(a, b):
            print(a, b)
            if all(lic in self.preferred for lic in b):
                return b
            else:
                return a

        for license, expected in self.tests:
            licenses = oe.license.flattened_licenses(None, license, choose)
            self.assertListEqual(licenses, expected)


class TestComplexCombinations(TestSimpleCombinations):
    tests = [
        (
            "LicenseRef-FOO AND (LicenseRef-BAR OR LicenseRef-BAZ) AND LicenseRef-MOO",
            ["LicenseRef-FOO", "LicenseRef-BAR", "LicenseRef-MOO"],
        ),
        (
            "(LicenseRef-ALPHA OR (LicenseRef-BETA AND LicenseRef-THETA) OR LicenseRef-OMEGA) AND LicenseRef-DELTA",
            ["LicenseRef-OMEGA", "LicenseRef-DELTA"],
        ),
        (
            "((LicenseRef-ALPHA OR LicenseRef-BETA) AND LicenseRef-FOO) OR LicenseRef-BAZ",
            ["LicenseRef-BETA", "LicenseRef-FOO"],
        ),
        (
            "(GPL-2.0-only OR LicenseRef-Proprietary) AND BSD-4-clause AND MIT",
            ["GPL-2.0-only", "BSD-4-Clause", "MIT"],
        ),
    ]
    preferred = [
        "LicenseRef-BAR",
        "LicenseRef-OMEGA",
        "LicenseRef-BETA",
        "GPL-2.0-only",
    ]


class TestIsIncluded(TestCase):
    tests = [
        (
            "LicenseRef-FOO OR LicenseRef-BAR",
            None,
            None,
            True,
            ["LicenseRef-FOO"],
        ),
        (
            "LicenseRef-FOO OR LicenseRef-BAR",
            None,
            "LicenseRef-FOO",
            True,
            ["LicenseRef-BAR"],
        ),
        (
            "LicenseRef-FOO OR LicenseRef-BAR",
            "LicenseRef-BAR",
            None,
            True,
            ["LicenseRef-BAR"],
        ),
        (
            "LicenseRef-FOO OR LicenseRef-BAR AND LicenseRef-FOOBAR",
            "*BAR",
            None,
            True,
            ["LicenseRef-BAR", "LicenseRef-FOOBAR"],
        ),
        (
            "LicenseRef-FOO OR LicenseRef-BAR AND LicenseRef-FOOBAR",
            None,
            "LicenseRef-FOO*",
            False,
            ["LicenseRef-FOOBAR"],
        ),
        (
            "(LicenseRef-FOO OR LicenseRef-BAR) AND LicenseRef-FOOBAR OR LicenseRef-BARFOO",
            None,
            "LicenseRef-FOO",
            True,
            ["LicenseRef-BAR", "LicenseRef-FOOBAR"],
        ),
        (
            "(FOO OR BAR) AND FOOBAR OR BAZ AND MOO AND BARFOO",
            None,
            "FOO",
            True,
            ["LicenseRef-BAZ", "LicenseRef-MOO", "LicenseRef-BARFOO"],
        ),
        (
            "GPL-3.0-or-later AND GPL-2.0-only AND LGPL-2.1-only OR Proprietary",
            None,
            None,
            True,
            ["GPL-3.0-or-later", "GPL-2.0-only", "LGPL-2.1-only"],
        ),
        (
            "GPL-3.0-or-later AND GPL-2.0-only AND LGPL-2.1-only OR Proprietary",
            None,
            "GPL-3.0-or-later",
            True,
            ["LicenseRef-Proprietary"],
        ),
        (
            "GPL-3.0-or-later AND GPL-2.0-only AND LGPL-2.1-only OR Proprietary",
            None,
            "GPL-3.0-or-later LicenseRef-Proprietary",
            False,
            ["GPL-3.0-or-later"],
        ),
    ]

    def test_tests(self):
        for (
            expression,
            include,
            exclude,
            expect_included,
            expect_licenses,
        ) in self.tests:
            actual_included, actual_licenses = oe.license.is_included(
                None,
                expression,
                (include or "").split(),
                (exclude or "").split(),
            )
            self.assertEqual(actual_included, expect_included)
            self.assertListEqual(actual_licenses, expect_licenses)


class TestSort(TestCase):
    tests = [
        (
            "MIT AND MIT",
            "MIT",
        ),
        (
            "MIT OR MIT",
            "MIT",
        ),
        (
            "MIT AND 0BSD",
            "0BSD AND MIT",
        ),
        (
            "MIT OR 0BSD",
            "0BSD OR MIT",
        ),
        (
            "(MIT AND 0BSD) AND (MIT AND 0BSD)",
            "0BSD AND MIT",
        ),
        (
            "(MIT AND 0BSD) OR (MIT AND GPL-3.0-or-later)",
            "0BSD AND MIT OR GPL-3.0-or-later AND MIT",
        ),
        (
            "(MIT OR 0BSD) AND (MIT OR GPL-3.0-or-later)",
            "(0BSD OR MIT) AND (GPL-3.0-or-later OR MIT)",
        ),
        (
            "(MIT AND 0BSD) AND (MIT AND GPL-3.0-or-later)",
            "0BSD AND GPL-3.0-or-later AND MIT",
        ),
        (
            "(MIT OR 0BSD) OR (MIT OR GPL-3.0-or-later)",
            "0BSD OR GPL-3.0-or-later OR MIT",
        ),
        (
            "((MIT OR 0BSD) OR MIT) OR GPL-3.0-or-later",
            "0BSD OR GPL-3.0-or-later OR MIT",
        ),
        (
            "MIT OR (0BSD OR (MIT OR GPL-3.0-or-later))",
            "0BSD OR GPL-3.0-or-later OR MIT",
        ),
        (
            "MIT OR (0BSD OR MIT) OR GPL-3.0-or-later",
            "0BSD OR GPL-3.0-or-later OR MIT",
        ),
        (
            "(MIT AND (0BSD OR MIT)) AND (0BSD AND GPL-3.0-or-later)",
            "0BSD AND GPL-3.0-or-later AND MIT AND (0BSD OR MIT)",
        ),
        (
            "(MIT OR (0BSD AND MIT)) OR (0BSD OR GPL-3.0-or-later)",
            "0BSD AND MIT OR 0BSD OR GPL-3.0-or-later OR MIT",
        ),
        (
            "(MIT AND ((0BSD OR MIT) AND LGPL-3.0-or-later)) AND (0BSD AND GPL-3.0-or-later)",
            "0BSD AND GPL-3.0-or-later AND LGPL-3.0-or-later AND MIT AND (0BSD OR MIT)",
        ),
        (
            "(MIT AND ((0BSD OR MIT) AND LGPL-3.0-or-later)) AND ((0BSD AND (0BSD OR MIT)) AND GPL-3.0-or-later)",
            "0BSD AND GPL-3.0-or-later AND LGPL-3.0-or-later AND MIT AND (0BSD OR MIT)",
        ),
        (
            # GPL-3.0-later is split across an otherwise sorted list; make sure
            # it can be detected and merged
            "(0BSD AND GPL-3.0-or-later) AND (GPL-3.0-or-later AND MIT)",
            "0BSD AND GPL-3.0-or-later AND MIT",
        ),
        (
            "GPL-3.0-or-later WITH GCC-exception-3.1 AND GPL-3.0-or-later WITH GCC-exception-3.1",
            "GPL-3.0-or-later WITH GCC-exception-3.1",
        ),
        (
            "GPL-3.0-or-later WITH GCC-exception-3.1 AND GPL-3.0-or-later",
            "GPL-3.0-or-later AND GPL-3.0-or-later WITH GCC-exception-3.1",
        ),
    ]

    def test_sort(self):
        for expression, expected in self.tests:
            actual = oe.spdx_license.parse(expression).sort()
            self.assertEqual(actual.to_string(), expected)
