from oe.cve_check import Version
from oeqa.selftest.case import OESelftestTestCase

class CVECheck(OESelftestTestCase):

    def test_version_compare(self):
        result = Version("100") > Version("99")
        self.assertTrue( result, msg="Failed to compare version '100' > '99'")
        result = Version("2.3.1") > Version("2.2.3")
        self.assertTrue( result, msg="Failed to compare version '2.3.1' > '2.2.3'")
        result = Version("2021-01-21") > Version("2020-12-25")
        self.assertTrue( result, msg="Failed to compare version '2021-01-21' > '2020-12-25'")
        result = Version("1.2-20200910") < Version("1.2-20200920")
        self.assertTrue( result, msg="Failed to compare version '1.2-20200910' < '1.2-20200920'")

        result = Version("1.0") >= Version("1.0beta")
        self.assertTrue( result, msg="Failed to compare version '1.0' >= '1.0beta'")
        result = Version("1.0-rc2") > Version("1.0-rc1")
        self.assertTrue( result, msg="Failed to compare version '1.0-rc2' > '1.0-rc1'")
        result = Version("1.0.alpha1") < Version("1.0")
        self.assertTrue( result, msg="Failed to compare version '1.0.alpha1' < '1.0'")
        result = Version("1.0_dev") <= Version("1.0")
        self.assertTrue( result, msg="Failed to compare version '1.0_dev' <= '1.0'")

        # ignore "p1" and "p2", so these should be equal
        result = Version("1.0p2") == Version("1.0p1")
        self.assertTrue( result ,msg="Failed to compare version '1.0p2' to '1.0p1'")
        # ignore the "b" and "r"
        result = Version("1.0b") == Version("1.0r")
        self.assertTrue( result ,msg="Failed to compare version '1.0b' to '1.0r'")

        # consider the trailing alphabet as patched level when comparing
        result = Version("1.0b","alphabetical") < Version("1.0r","alphabetical")
        self.assertTrue( result ,msg="Failed to compare version with suffix '1.0b' < '1.0r'")
        result = Version("1.0b","alphabetical") > Version("1.0","alphabetical")
        self.assertTrue( result ,msg="Failed to compare version with suffix '1.0b' > '1.0'")
