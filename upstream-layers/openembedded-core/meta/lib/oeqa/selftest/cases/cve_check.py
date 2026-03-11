#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

import json
import os
from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import bitbake, get_bb_vars

class CVECheck(OESelftestTestCase):

    def test_version_compare(self):
        from oe.cve_check import Version

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

        # consider the trailing "p" and "patch" as patched released when comparing
        result = Version("1.0","patch") < Version("1.0p1","patch")
        self.assertTrue( result ,msg="Failed to compare version with suffix '1.0' < '1.0p1'")
        result = Version("1.0p2","patch") > Version("1.0p1","patch")
        self.assertTrue( result ,msg="Failed to compare version with suffix '1.0p2' > '1.0p1'")
        result = Version("1.0_patch2","patch") < Version("1.0_patch3","patch")
        self.assertTrue( result ,msg="Failed to compare version with suffix '1.0_patch2' < '1.0_patch3'")


    def test_convert_cve_version(self):
        from oe.cve_check import convert_cve_version

        # Default format
        self.assertEqual(convert_cve_version("8.3"), "8.3")
        self.assertEqual(convert_cve_version(""), "")

        # OpenSSL format version
        self.assertEqual(convert_cve_version("1.1.1t"), "1.1.1t")

        # OpenSSH format
        self.assertEqual(convert_cve_version("8.3_p1"), "8.3p1")
        self.assertEqual(convert_cve_version("8.3_p22"), "8.3p22")

        # Linux kernel format
        self.assertEqual(convert_cve_version("6.2_rc8"), "6.2-rc8")
        self.assertEqual(convert_cve_version("6.2_rc31"), "6.2-rc31")

    def test_product_match(self):
        from oe.cve_check import has_cve_product_match

        status = {}
        status["detail"] = "ignored"
        status["vendor"] = "*"
        status["product"] = "*"
        status["description"] = ""
        status["mapping"] = ""

        self.assertEqual(has_cve_product_match(status, "some_vendor:some_product"), True)
        self.assertEqual(has_cve_product_match(status, "*:*"), True)
        self.assertEqual(has_cve_product_match(status, "some_product"), True)
        self.assertEqual(has_cve_product_match(status, "glibc"), True)
        self.assertEqual(has_cve_product_match(status, "glibca"), True)
        self.assertEqual(has_cve_product_match(status, "aglibc"), True)
        self.assertEqual(has_cve_product_match(status, "*"), True)
        self.assertEqual(has_cve_product_match(status, "aglibc glibc test:test"), True)

        status["product"] = "glibc"
        self.assertEqual(has_cve_product_match(status, "some_vendor:some_product"), False)
        # The CPE in the recipe must be defined, no * accepted
        self.assertEqual(has_cve_product_match(status, "*:*"), False)
        self.assertEqual(has_cve_product_match(status, "*"), False)
        self.assertEqual(has_cve_product_match(status, "some_product"), False)
        self.assertEqual(has_cve_product_match(status, "glibc"), True)
        self.assertEqual(has_cve_product_match(status, "glibca"), False)
        self.assertEqual(has_cve_product_match(status, "aglibc"), False)
        self.assertEqual(has_cve_product_match(status, "some_vendor:glibc"), True)
        self.assertEqual(has_cve_product_match(status, "some_vendor:glibc test"), True)
        self.assertEqual(has_cve_product_match(status, "test some_vendor:glibc"), True)

        status["vendor"] = "glibca"
        status["product"] = "glibc"
        self.assertEqual(has_cve_product_match(status, "some_vendor:some_product"), False)
        # The CPE in the recipe must be defined, no * accepted
        self.assertEqual(has_cve_product_match(status, "*:*"), False)
        self.assertEqual(has_cve_product_match(status, "*"), False)
        self.assertEqual(has_cve_product_match(status, "some_product"), False)
        self.assertEqual(has_cve_product_match(status, "glibc"), False)
        self.assertEqual(has_cve_product_match(status, "glibca"), False)
        self.assertEqual(has_cve_product_match(status, "aglibc"), False)
        self.assertEqual(has_cve_product_match(status, "some_vendor:glibc"), False)
        self.assertEqual(has_cve_product_match(status, "glibca:glibc"), True)
        self.assertEqual(has_cve_product_match(status, "test:test glibca:glibc"), True)
        self.assertEqual(has_cve_product_match(status, "test glibca:glibc"), True)
        self.assertEqual(has_cve_product_match(status, "glibca:glibc test"), True)

    def test_parse_cve_from_patch_filename(self):
        from oe.cve_check import parse_cve_from_filename

        # Patch filename without CVE ID
        self.assertEqual(parse_cve_from_filename("0001-test.patch"), "")

        # Patch with single CVE ID
        self.assertEqual(
            parse_cve_from_filename("CVE-2022-12345.patch"), "CVE-2022-12345"
        )

        # Patch with multiple CVE IDs
        self.assertEqual(
            parse_cve_from_filename("CVE-2022-41741-CVE-2022-41742.patch"),
            "CVE-2022-41742",
        )

        # Patches with CVE ID and appended text
        self.assertEqual(
            parse_cve_from_filename("CVE-2023-3019-0001.patch"), "CVE-2023-3019"
        )
        self.assertEqual(
            parse_cve_from_filename("CVE-2024-21886-1.patch"), "CVE-2024-21886"
        )

        # Patch with CVE ID and prepended text
        self.assertEqual(
            parse_cve_from_filename("grep-CVE-2012-5667.patch"), "CVE-2012-5667"
        )
        self.assertEqual(
            parse_cve_from_filename("0001-CVE-2012-5667.patch"), "CVE-2012-5667"
        )

        # Patch with CVE ID and both prepended and appended text
        self.assertEqual(
            parse_cve_from_filename(
                "0001-tpm2_import-fix-fixed-AES-key-CVE-2021-3565-0001.patch"
            ),
            "CVE-2021-3565",
        )

        # Only grab the last CVE ID in the filename
        self.assertEqual(
            parse_cve_from_filename("CVE-2012-5667-CVE-2012-5668.patch"),
            "CVE-2012-5668",
        )

        # Test invalid CVE ID with incorrect length (must be at least 4 digits)
        self.assertEqual(
            parse_cve_from_filename("CVE-2024-001.patch"),
            "",
        )

        # Test valid CVE ID with very long length
        self.assertEqual(
            parse_cve_from_filename("CVE-2024-0000000000000000000000001.patch"),
            "CVE-2024-0000000000000000000000001",
        )

    def test_parse_cve_from_patch_contents(self):
        import textwrap
        from oe.cve_check import parse_cves_from_patch_contents

        # Standard patch file excerpt without any patches
        self.assertEqual(
            parse_cves_from_patch_contents(
                textwrap.dedent("""\
            remove "*" for root since we don't have a /etc/shadow so far.

            Upstream-Status: Inappropriate [configuration]

            Signed-off-by: Scott Garman <scott.a.garman@intel.com>

            --- base-passwd/passwd.master~nobash
            +++ base-passwd/passwd.master
            @@ -1,4 +1,4 @@
            -root:*:0:0:root:/root:/bin/sh
            +root::0:0:root:/root:/bin/sh
            daemon:*:1:1:daemon:/usr/sbin:/bin/sh
            bin:*:2:2:bin:/bin:/bin/sh
            sys:*:3:3:sys:/dev:/bin/sh
            """)
            ),
            set(),
        )

        # Patch file with multiple CVE IDs (space-separated)
        self.assertEqual(
            parse_cves_from_patch_contents(
                textwrap.dedent("""\
                There is an assertion in function _cairo_arc_in_direction().

                CVE: CVE-2019-6461 CVE-2019-6462
                Upstream-Status: Pending
                Signed-off-by: Ross Burton <ross.burton@intel.com>

                diff --git a/src/cairo-arc.c b/src/cairo-arc.c
                index 390397bae..1bde774a4 100644
                --- a/src/cairo-arc.c
                +++ b/src/cairo-arc.c
                @@ -186,7 +186,8 @@ _cairo_arc_in_direction (cairo_t          *cr,
                    if (cairo_status (cr))
                        return;

                -    assert (angle_max >= angle_min);
                +    if (angle_max < angle_min)
                +       return;

                    if (angle_max - angle_min > 2 * M_PI * MAX_FULL_CIRCLES) {
                    angle_max = fmod (angle_max - angle_min, 2 * M_PI);
            """),
            ),
            {"CVE-2019-6461", "CVE-2019-6462"},
        )

        # Patch file with multiple CVE IDs (comma-separated w/ both space and no space)
        self.assertEqual(
            parse_cves_from_patch_contents(
                textwrap.dedent("""\
                There is an assertion in function _cairo_arc_in_direction().

                CVE: CVE-2019-6461,CVE-2019-6462, CVE-2019-6463
                Upstream-Status: Pending
                Signed-off-by: Ross Burton <ross.burton@intel.com>

                diff --git a/src/cairo-arc.c b/src/cairo-arc.c
                index 390397bae..1bde774a4 100644
                --- a/src/cairo-arc.c
                +++ b/src/cairo-arc.c
                @@ -186,7 +186,8 @@ _cairo_arc_in_direction (cairo_t          *cr,
                    if (cairo_status (cr))
                        return;

                -    assert (angle_max >= angle_min);
                +    if (angle_max < angle_min)
                +       return;

                    if (angle_max - angle_min > 2 * M_PI * MAX_FULL_CIRCLES) {
                    angle_max = fmod (angle_max - angle_min, 2 * M_PI);

            """),
            ),
            {"CVE-2019-6461", "CVE-2019-6462", "CVE-2019-6463"},
        )

        # Patch file with multiple CVE IDs (&-separated)
        self.assertEqual(
            parse_cves_from_patch_contents(
                textwrap.dedent("""\
                There is an assertion in function _cairo_arc_in_direction().

                CVE: CVE-2019-6461 & CVE-2019-6462
                Upstream-Status: Pending
                Signed-off-by: Ross Burton <ross.burton@intel.com>

                diff --git a/src/cairo-arc.c b/src/cairo-arc.c
                index 390397bae..1bde774a4 100644
                --- a/src/cairo-arc.c
                +++ b/src/cairo-arc.c
                @@ -186,7 +186,8 @@ _cairo_arc_in_direction (cairo_t          *cr,
                    if (cairo_status (cr))
                        return;

                -    assert (angle_max >= angle_min);
                +    if (angle_max < angle_min)
                +       return;

                    if (angle_max - angle_min > 2 * M_PI * MAX_FULL_CIRCLES) {
                    angle_max = fmod (angle_max - angle_min, 2 * M_PI);
            """),
            ),
            {"CVE-2019-6461", "CVE-2019-6462"},
        )

        # Patch file with multiple lines with CVE IDs
        self.assertEqual(
            parse_cves_from_patch_contents(
                textwrap.dedent("""\
                There is an assertion in function _cairo_arc_in_direction().

                CVE: CVE-2019-6461 & CVE-2019-6462

                CVE: CVE-2019-6463 & CVE-2019-6464
                Upstream-Status: Pending
                Signed-off-by: Ross Burton <ross.burton@intel.com>

                diff --git a/src/cairo-arc.c b/src/cairo-arc.c
                index 390397bae..1bde774a4 100644
                --- a/src/cairo-arc.c
                +++ b/src/cairo-arc.c
                @@ -186,7 +186,8 @@ _cairo_arc_in_direction (cairo_t          *cr,
                    if (cairo_status (cr))
                        return;

                -    assert (angle_max >= angle_min);
                +    if (angle_max < angle_min)
                +       return;

                    if (angle_max - angle_min > 2 * M_PI * MAX_FULL_CIRCLES) {
                    angle_max = fmod (angle_max - angle_min, 2 * M_PI);

            """),
            ),
            {"CVE-2019-6461", "CVE-2019-6462", "CVE-2019-6463", "CVE-2019-6464"},
        )

    def test_recipe_report_json(self):
        config = """
INHERIT += "cve-check"
CVE_CHECK_FORMAT_JSON = "1"
"""
        self.write_config(config)

        vars = get_bb_vars(["CVE_CHECK_SUMMARY_DIR", "CVE_CHECK_SUMMARY_FILE_NAME_JSON"])
        summary_json = os.path.join(vars["CVE_CHECK_SUMMARY_DIR"], vars["CVE_CHECK_SUMMARY_FILE_NAME_JSON"])
        recipe_json = os.path.join(vars["CVE_CHECK_SUMMARY_DIR"], "m4-native_cve.json")

        try:
            os.remove(summary_json)
            os.remove(recipe_json)
        except FileNotFoundError:
            pass

        bitbake("m4-native -c cve_check")

        def check_m4_json(filename):
            with open(filename) as f:
                report = json.load(f)
            self.assertEqual(report["version"], "1")
            self.assertEqual(len(report["package"]), 1)
            package = report["package"][0]
            self.assertEqual(package["name"], "m4-native")
            found_cves = { issue["id"]: issue["status"] for issue in package["issue"]}
            self.assertIn("CVE-2008-1687", found_cves)
            self.assertEqual(found_cves["CVE-2008-1687"], "Patched")

        self.assertExists(summary_json)
        check_m4_json(summary_json)
        self.assertExists(recipe_json)
        check_m4_json(recipe_json)


    def test_image_json(self):
        config = """
INHERIT += "cve-check"
CVE_CHECK_FORMAT_JSON = "1"
"""
        self.write_config(config)

        vars = get_bb_vars(["CVE_CHECK_DIR", "CVE_CHECK_SUMMARY_DIR", "CVE_CHECK_SUMMARY_FILE_NAME_JSON"])
        report_json = os.path.join(vars["CVE_CHECK_SUMMARY_DIR"], vars["CVE_CHECK_SUMMARY_FILE_NAME_JSON"])
        print(report_json)
        try:
            os.remove(report_json)
        except FileNotFoundError:
            pass

        bitbake("core-image-minimal-initramfs")
        self.assertExists(report_json)

        # Check that the summary report lists at least one package
        with open(report_json) as f:
            report = json.load(f)
        self.assertEqual(report["version"], "1")
        self.assertGreater(len(report["package"]), 1)

        # Check that a random recipe wrote a recipe report to deploy/cve/
        recipename = report["package"][0]["name"]
        recipe_report = os.path.join(vars["CVE_CHECK_DIR"], recipename + "_cve.json")
        self.assertExists(recipe_report)
        with open(recipe_report) as f:
            report = json.load(f)
        self.assertEqual(report["version"], "1")
        self.assertEqual(len(report["package"]), 1)
        self.assertEqual(report["package"][0]["name"], recipename)


    def test_recipe_report_json_unpatched(self):
        config = """
INHERIT += "cve-check"
CVE_CHECK_FORMAT_JSON = "1"
CVE_CHECK_REPORT_PATCHED = "0"
"""
        self.write_config(config)

        vars = get_bb_vars(["CVE_CHECK_SUMMARY_DIR", "CVE_CHECK_SUMMARY_FILE_NAME_JSON"])
        summary_json = os.path.join(vars["CVE_CHECK_SUMMARY_DIR"], vars["CVE_CHECK_SUMMARY_FILE_NAME_JSON"])
        recipe_json = os.path.join(vars["CVE_CHECK_SUMMARY_DIR"], "m4-native_cve.json")

        try:
            os.remove(summary_json)
            os.remove(recipe_json)
        except FileNotFoundError:
            pass

        bitbake("m4-native -c cve_check")

        def check_m4_json(filename):
            with open(filename) as f:
                report = json.load(f)
            self.assertEqual(report["version"], "1")
            self.assertEqual(len(report["package"]), 1)
            package = report["package"][0]
            self.assertEqual(package["name"], "m4-native")
            #m4 had only Patched CVEs, so the issues array will be empty
            self.assertEqual(package["issue"], [])

        self.assertExists(summary_json)
        check_m4_json(summary_json)
        self.assertExists(recipe_json)
        check_m4_json(recipe_json)


    def test_recipe_report_json_ignored(self):
        config = """
INHERIT += "cve-check"
CVE_CHECK_FORMAT_JSON = "1"
CVE_CHECK_REPORT_PATCHED = "1"
"""
        self.write_config(config)

        vars = get_bb_vars(["CVE_CHECK_SUMMARY_DIR", "CVE_CHECK_SUMMARY_FILE_NAME_JSON"])
        summary_json = os.path.join(vars["CVE_CHECK_SUMMARY_DIR"], vars["CVE_CHECK_SUMMARY_FILE_NAME_JSON"])
        recipe_json = os.path.join(vars["CVE_CHECK_SUMMARY_DIR"], "logrotate_cve.json")

        try:
            os.remove(summary_json)
            os.remove(recipe_json)
        except FileNotFoundError:
            pass

        bitbake("logrotate -c cve_check")

        def check_m4_json(filename):
            with open(filename) as f:
                report = json.load(f)
            self.assertEqual(report["version"], "1")
            self.assertEqual(len(report["package"]), 1)
            package = report["package"][0]
            self.assertEqual(package["name"], "logrotate")
            found_cves = {}
            for issue in package["issue"]:
                found_cves[issue["id"]] = {
                    "status" : issue["status"],
                    "detail" : issue["detail"] if "detail" in issue else "",
                    "description" : issue["description"] if "description" in issue else ""
                }
            # m4 CVE should not be in logrotate
            self.assertNotIn("CVE-2008-1687", found_cves)
            # logrotate has both Patched and Ignored CVEs
            detail = "version-not-in-range"
            self.assertIn("CVE-2011-1098", found_cves)
            self.assertEqual(found_cves["CVE-2011-1098"]["status"], "Patched")
            self.assertEqual(found_cves["CVE-2011-1098"]["detail"], detail)
            self.assertEqual(len(found_cves["CVE-2011-1098"]["description"]), 0)
            detail = "not-applicable-platform"
            description = "CVE is debian, gentoo or SUSE specific on the way logrotate was installed/used"
            self.assertIn("CVE-2011-1548", found_cves)
            self.assertEqual(found_cves["CVE-2011-1548"]["status"], "Ignored")
            self.assertEqual(found_cves["CVE-2011-1548"]["detail"], detail)
            self.assertEqual(found_cves["CVE-2011-1548"]["description"], description)
            self.assertIn("CVE-2011-1549", found_cves)
            self.assertEqual(found_cves["CVE-2011-1549"]["status"], "Ignored")
            self.assertEqual(found_cves["CVE-2011-1549"]["detail"], detail)
            self.assertEqual(found_cves["CVE-2011-1549"]["description"], description)
            self.assertIn("CVE-2011-1550", found_cves)
            self.assertEqual(found_cves["CVE-2011-1550"]["status"], "Ignored")
            self.assertEqual(found_cves["CVE-2011-1550"]["detail"], detail)
            self.assertEqual(found_cves["CVE-2011-1550"]["description"], description)

        self.assertExists(summary_json)
        check_m4_json(summary_json)
        self.assertExists(recipe_json)
        check_m4_json(recipe_json)
