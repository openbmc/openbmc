#
# SPDX-License-Identifier: MIT
#

import sys
from unittest.case import TestCase
from contextlib import contextmanager
from io import StringIO
from oe.utils import packages_filter_out_system, trim_version, multiprocess_launch

class TestPackagesFilterOutSystem(TestCase):
    def test_filter(self):
        """
        Test that oe.utils.packages_filter_out_system works.
        """
        try:
            import bb
        except ImportError:
            self.skipTest("Cannot import bb")

        d = bb.data_smart.DataSmart()
        d.setVar("PN", "foo")

        d.setVar("PACKAGES", "foo foo-doc foo-dev")
        pkgs = packages_filter_out_system(d)
        self.assertEqual(pkgs, [])

        d.setVar("PACKAGES", "foo foo-doc foo-data foo-dev")
        pkgs = packages_filter_out_system(d)
        self.assertEqual(pkgs, ["foo-data"])

        d.setVar("PACKAGES", "foo foo-locale-en-gb")
        pkgs = packages_filter_out_system(d)
        self.assertEqual(pkgs, [])

        d.setVar("PACKAGES", "foo foo-data foo-locale-en-gb")
        pkgs = packages_filter_out_system(d)
        self.assertEqual(pkgs, ["foo-data"])


class TestTrimVersion(TestCase):
    def test_version_exception(self):
        with self.assertRaises(TypeError):
            trim_version(None, 2)
        with self.assertRaises(TypeError):
            trim_version((1, 2, 3), 2)

    def test_num_exception(self):
        with self.assertRaises(ValueError):
            trim_version("1.2.3", 0)
        with self.assertRaises(ValueError):
            trim_version("1.2.3", -1)

    def test_valid(self):
        self.assertEqual(trim_version("1.2.3", 1), "1")
        self.assertEqual(trim_version("1.2.3", 2), "1.2")
        self.assertEqual(trim_version("1.2.3", 3), "1.2.3")
        self.assertEqual(trim_version("1.2.3", 4), "1.2.3")


class TestMultiprocessLaunch(TestCase):

    def test_multiprocesslaunch(self):
        import bb

        def testfunction(item, d):
            if item == "2":
                raise KeyError("Invalid number %s" % item)
            return "Found %s" % item

        def dummyerror(msg):
            print("ERROR: %s" % msg)
        def dummyfatal(msg):
            print("ERROR: %s" % msg)
            raise bb.BBHandledException()

        @contextmanager
        def captured_output():
            new_out, new_err = StringIO(), StringIO()
            old_out, old_err = sys.stdout, sys.stderr
            try:
                sys.stdout, sys.stderr = new_out, new_err
                yield sys.stdout, sys.stderr
            finally:
                sys.stdout, sys.stderr = old_out, old_err

        d = bb.data_smart.DataSmart()
        bb.error = dummyerror
        bb.fatal = dummyfatal

        # Assert the function returns the right results
        result = multiprocess_launch(testfunction, ["3", "4", "5", "6"], d, extraargs=(d,))
        self.assertIn("Found 3", result)
        self.assertIn("Found 4", result)
        self.assertIn("Found 5", result)
        self.assertIn("Found 6", result)
        self.assertEqual(len(result), 4)

        # Assert the function prints exceptions
        with captured_output() as (out, err):
            self.assertRaises(bb.BBHandledException, multiprocess_launch, testfunction, ["1", "2", "3", "4", "5", "6"], d, extraargs=(d,))
        self.assertIn("KeyError: 'Invalid number 2'", out.getvalue())
