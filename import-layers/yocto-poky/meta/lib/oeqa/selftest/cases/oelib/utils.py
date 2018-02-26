from unittest.case import TestCase
from oe.utils import packages_filter_out_system, trim_version

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
