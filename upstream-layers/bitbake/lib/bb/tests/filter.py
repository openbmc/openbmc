#
# Copyright (C) 2025 Garmin Ltd. or its subsidiaries
#
# SPDX-License-Identifier: GPL-2.0-only
#

import unittest
import bb.filter


class BuiltinFilterTest(unittest.TestCase):
    def test_disallowed_builtins(self):
        with self.assertRaises(NameError):
            val = bb.filter.apply_filters("1", ["open('foo.txt', 'rb')"])

    def test_prefix(self):
        val = bb.filter.apply_filters("1 2 3", ["prefix(val, 'a')"])
        self.assertEqual(val, "a1 a2 a3")

        val = bb.filter.apply_filters("", ["prefix(val, 'a')"])
        self.assertEqual(val, "")

    def test_suffix(self):
        val = bb.filter.apply_filters("1 2 3", ["suffix(val, 'b')"])
        self.assertEqual(val, "1b 2b 3b")

        val = bb.filter.apply_filters("", ["suffix(val, 'b')"])
        self.assertEqual(val, "")

    def test_sort(self):
        val = bb.filter.apply_filters("z y x", ["sort(val)"])
        self.assertEqual(val, "x y z")

        val = bb.filter.apply_filters("", ["sort(val)"])
        self.assertEqual(val, "")

    def test_identity(self):
        val = bb.filter.apply_filters("1 2 3", ["val"])
        self.assertEqual(val, "1 2 3")

        val = bb.filter.apply_filters("123", ["val"])
        self.assertEqual(val, "123")

    def test_empty(self):
        val = bb.filter.apply_filters("1 2 3", ["", "prefix(val, 'a')", ""])
        self.assertEqual(val, "a1 a2 a3")

    def test_nested(self):
        val = bb.filter.apply_filters("1 2 3", ["prefix(prefix(val, 'a'), 'b')"])
        self.assertEqual(val, "ba1 ba2 ba3")

        val = bb.filter.apply_filters("1 2 3", ["prefix(prefix(val, 'b'), 'a')"])
        self.assertEqual(val, "ab1 ab2 ab3")

    def test_filter_order(self):
        val = bb.filter.apply_filters("1 2 3", ["prefix(val, 'a')", "prefix(val, 'b')"])
        self.assertEqual(val, "ba1 ba2 ba3")

        val = bb.filter.apply_filters("1 2 3", ["prefix(val, 'b')", "prefix(val, 'a')"])
        self.assertEqual(val, "ab1 ab2 ab3")

        val = bb.filter.apply_filters("1 2 3", ["prefix(val, 'a')", "suffix(val, 'b')"])
        self.assertEqual(val, "a1b a2b a3b")

        val = bb.filter.apply_filters("1 2 3", ["suffix(val, 'b')", "prefix(val, 'a')"])
        self.assertEqual(val, "a1b a2b a3b")

    def test_remove(self):
        val = bb.filter.apply_filters("1 2 3", ["remove(val, ['2'])"])
        self.assertEqual(val, "1 3")

        val = bb.filter.apply_filters("1,2,3", ["remove(val, ['2'], ',')"])
        self.assertEqual(val, "1,3")

        val = bb.filter.apply_filters("1 2 3", ["remove(val, ['4'])"])
        self.assertEqual(val, "1 2 3")

        val = bb.filter.apply_filters("1 2 3", ["remove(val, ['1', '2'])"])
        self.assertEqual(val, "3")

        val = bb.filter.apply_filters("1 2 3", ["remove(val, '2')"])
        self.assertEqual(val, "1 3")

        val = bb.filter.apply_filters("1 2 3", ["remove(val, '4')"])
        self.assertEqual(val, "1 2 3")

        val = bb.filter.apply_filters("1 2 3", ["remove(val, '1 2')"])
        self.assertEqual(val, "3")
