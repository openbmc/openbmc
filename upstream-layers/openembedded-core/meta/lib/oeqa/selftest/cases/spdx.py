#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

import textwrap
import hashlib
from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import bitbake, get_bb_var, get_bb_vars
import oe.spdx30


class SPDX3CheckBase(object):
    """
    Base class for checking SPDX 3 based tests
    """

    def check_spdx_file(self, filename):
        self.assertExists(filename)

        # Read the file
        objset = oe.spdx30.SHACLObjectSet()
        with open(filename, "r") as f:
            d = oe.spdx30.JSONLDDeserializer()
            d.read(f, objset)

        return objset

    def check_recipe_spdx(self, target_name, spdx_path, *, task=None, extraconf=""):
        config = textwrap.dedent(
            f"""\
                INHERIT:remove = "create-spdx"
                INHERIT += "{self.SPDX_CLASS}"
                """
        ) + textwrap.dedent(extraconf)

        self.write_config(config)

        if task:
            bitbake(f"-c {task} {target_name}")
        else:
            bitbake(target_name)

        filename = spdx_path.format(
            **get_bb_vars(
                [
                    "DEPLOY_DIR_IMAGE",
                    "DEPLOY_DIR_SPDX",
                    "MACHINE",
                    "MACHINE_ARCH",
                    "SDKMACHINE",
                    "SDK_DEPLOY",
                    "SPDX_VERSION",
                    "SSTATE_PKGARCH",
                    "TOOLCHAIN_OUTPUTNAME",
                ],
                target_name,
            )
        )

        return self.check_spdx_file(filename)

    def check_objset_missing_ids(self, objset):
        for o in objset.foreach_type(oe.spdx30.SpdxDocument):
            doc = o
            break
        else:
            self.assertTrue(False, "Unable to find SpdxDocument")

        missing_ids = objset.missing_ids - set(i.externalSpdxId for i in doc.import_)
        if missing_ids:
            self.assertTrue(
                False,
                "The following SPDXIDs are unresolved:\n  " + "\n  ".join(missing_ids),
            )


class SPDX30Check(SPDX3CheckBase, OESelftestTestCase):
    SPDX_CLASS = "create-spdx-3.0"

    def test_base_files(self):
        self.check_recipe_spdx(
            "base-files",
            "{DEPLOY_DIR_SPDX}/{MACHINE_ARCH}/static/static-base-files.spdx.json",
            task="create_recipe_spdx",
        )
        self.check_recipe_spdx(
            "base-files",
            "{DEPLOY_DIR_SPDX}/{MACHINE_ARCH}/packages/package-base-files.spdx.json",
        )

    def test_world_sbom(self):
        objset = self.check_recipe_spdx(
            "meta-world-recipe-sbom",
            "{DEPLOY_DIR_IMAGE}/world-recipe-sbom.spdx.json",
        )

        # Document should be fully linked
        self.check_objset_missing_ids(objset)

    def test_gcc_include_source(self):
        objset = self.check_recipe_spdx(
            "gcc",
            "{DEPLOY_DIR_SPDX}/{SSTATE_PKGARCH}/builds/build-gcc.spdx.json",
            extraconf="""\
                SPDX_INCLUDE_SOURCES = "1"
                """,
        )

        gcc_pv = get_bb_var("PV", "gcc")
        filename = f"gcc-{gcc_pv}/README"
        found = False
        for software_file in objset.foreach_type(oe.spdx30.software_File):
            if software_file.name == filename:
                found = True
                self.logger.info(
                    f"The spdxId of {filename} in build-gcc.spdx.json is {software_file.spdxId}"
                )
                break

        self.assertTrue(
            found, f"Not found source file {filename} in build-gcc.spdx.json\n"
        )

    def test_core_image_minimal(self):
        objset = self.check_recipe_spdx(
            "core-image-minimal",
            "{DEPLOY_DIR_IMAGE}/core-image-minimal-{MACHINE}.rootfs.spdx.json",
        )

        # Document should be fully linked
        self.check_objset_missing_ids(objset)

    def test_core_image_minimal_sdk(self):
        objset = self.check_recipe_spdx(
            "core-image-minimal",
            "{SDK_DEPLOY}/{TOOLCHAIN_OUTPUTNAME}.spdx.json",
            task="populate_sdk",
        )

        # Document should be fully linked
        self.check_objset_missing_ids(objset)

    def test_baremetal_helloworld(self):
        objset = self.check_recipe_spdx(
            "baremetal-helloworld",
            "{DEPLOY_DIR_IMAGE}/baremetal-helloworld-image-{MACHINE}.spdx.json",
            extraconf="""\
                TCLIBC = "baremetal"
                """,
        )

        # Document should be fully linked
        self.check_objset_missing_ids(objset)

    def test_extra_opts(self):
        HOST_SPDXID = "http://foo.bar/spdx/bar2"

        EXTRACONF = textwrap.dedent(
            f"""\
            SPDX_INVOKED_BY_name = "CI Tool"
            SPDX_INVOKED_BY_type = "software"

            SPDX_ON_BEHALF_OF_name = "John Doe"
            SPDX_ON_BEHALF_OF_type = "person"
            SPDX_ON_BEHALF_OF_id_email = "John.Doe@noreply.com"

            SPDX_PACKAGE_SUPPLIER_name = "ACME Embedded Widgets"
            SPDX_PACKAGE_SUPPLIER_type = "organization"

            SPDX_AUTHORS += "authorA"
            SPDX_AUTHORS_authorA_ref = "SPDX_ON_BEHALF_OF"

            SPDX_BUILD_HOST = "host"

            SPDX_IMPORTS += "host"
            SPDX_IMPORTS_host_spdxid = "{HOST_SPDXID}"

            SPDX_INCLUDE_BUILD_VARIABLES = "1"
            SPDX_INCLUDE_BITBAKE_PARENT_BUILD = "1"
            SPDX_INCLUDE_TIMESTAMPS = "1"

            SPDX_PRETTY = "1"
            """
        )
        extraconf_hash = hashlib.sha1(EXTRACONF.encode("utf-8")).hexdigest()

        objset = self.check_recipe_spdx(
            "core-image-minimal",
            "{DEPLOY_DIR_IMAGE}/core-image-minimal-{MACHINE}.rootfs.spdx.json",
            # Many SPDX variables do not trigger a rebuild, since they are
            # intended to record information at the time of the build. As such,
            # the extra configuration alone may not trigger a rebuild, and even
            # if it does, the task hash won't necessarily be unique. In order
            # to make sure rebuilds happen, but still allow these test objects
            # to be pulled from sstate (e.g. remain reproducible), change the
            # namespace prefix to include the hash of the extra configuration
            extraconf=textwrap.dedent(
                f"""\
                SPDX_NAMESPACE_PREFIX = "http://spdx.org/spdxdocs/{extraconf_hash}"
                """
            )
            + EXTRACONF,
        )

        # Document should be fully linked
        self.check_objset_missing_ids(objset)

        for o in objset.foreach_type(oe.spdx30.SoftwareAgent):
            if o.name == "CI Tool":
                break
        else:
            self.assertTrue(False, "Unable to find software tool")

        for o in objset.foreach_type(oe.spdx30.Person):
            if o.name == "John Doe":
                break
        else:
            self.assertTrue(False, "Unable to find person")

        for o in objset.foreach_type(oe.spdx30.Organization):
            if o.name == "ACME Embedded Widgets":
                break
        else:
            self.assertTrue(False, "Unable to find organization")

        for o in objset.foreach_type(oe.spdx30.SpdxDocument):
            doc = o
            break
        else:
            self.assertTrue(False, "Unable to find SpdxDocument")

        for i in doc.import_:
            if i.externalSpdxId == HOST_SPDXID:
                break
        else:
            self.assertTrue(False, "Unable to find imported Host SpdxID")

    def test_custom_annotation_vars(self):
        """
        Test that SPDX_CUSTOM_ANNOTATION_VARS properly creates annotations
        without runtime errors. This is a regression test for the bug where
        new_annotation() was called as a standalone function instead of as
        a method on build_objset, causing a NameError.

        The test verifies:
        1. The build completes successfully (no NameError)
        2. Each configured annotation variable appears exactly once
        3. The annotation values match the configured variables

        We check for exact equality (not >=) to prevent regressions where
        one annotation might appear multiple times while another is missing.
        """
        ANNOTATION_VAR1 = "TestAnnotation1"
        ANNOTATION_VAR2 = "TestAnnotation2"

        # This will fail with NameError if new_annotation() is called incorrectly
        objset = self.check_recipe_spdx(
            "base-files",
            "{DEPLOY_DIR_SPDX}/{MACHINE_ARCH}/builds/build-base-files.spdx.json",
            extraconf=textwrap.dedent(
                f"""\
                ANNOTATION1 = "{ANNOTATION_VAR1}"
                ANNOTATION2 = "{ANNOTATION_VAR2}"
                SPDX_CUSTOM_ANNOTATION_VARS = "ANNOTATION1 ANNOTATION2"
                """
            ),
        )

        # If we got here, the build succeeded (no NameError)
        # Now verify the annotations were actually created

        # Find the build element
        build = None
        for o in objset.foreach_type(oe.spdx30.build_Build):
            build = o
            break

        self.assertIsNotNone(build, "Unable to find Build element")

        # Find annotation objects that reference our build
        found_annotations = []
        for obj in objset.objects:  # <-- Remove parentheses
            if isinstance(obj, oe.spdx30.Annotation):
                if hasattr(obj, "subject") and build._id == obj.subject._id:
                    found_annotations.append(obj)

        # Check each annotation separately to ensure exactly one occurrence of each
        annotation1_count = 0
        annotation2_count = 0

        for annotation in found_annotations:
            if hasattr(annotation, "statement"):
                if f"ANNOTATION1={ANNOTATION_VAR1}" in annotation.statement:
                    annotation1_count += 1
                    self.logger.info(f"Found ANNOTATION1: {annotation.statement}")
                if f"ANNOTATION2={ANNOTATION_VAR2}" in annotation.statement:
                    annotation2_count += 1
                    self.logger.info(f"Found ANNOTATION2: {annotation.statement}")

        # Each annotation should appear exactly once
        self.assertEqual(
            annotation1_count,
            1,
            f"Expected exactly 1 occurrence of ANNOTATION1, found {annotation1_count}",
        )
        self.assertEqual(
            annotation2_count,
            1,
            f"Expected exactly 1 occurrence of ANNOTATION2, found {annotation2_count}",
        )

    def test_kernel_config_spdx(self):
        kernel_recipe = get_bb_var("PREFERRED_PROVIDER_virtual/kernel")
        spdx_file = f"build-{kernel_recipe}.spdx.json"
        spdx_path = f"{{DEPLOY_DIR_SPDX}}/{{SSTATE_PKGARCH}}/builds/{spdx_file}"

        # Make sure kernel is configured first
        bitbake(f"-c configure {kernel_recipe}")

        objset = self.check_recipe_spdx(
            kernel_recipe,
            spdx_path,
            task="do_create_spdx",
            extraconf="""\
                INHERIT += "create-spdx"
                SPDX_INCLUDE_KERNEL_CONFIG = "1"
                """,
        )

        # Check that at least one CONFIG_* entry exists
        found_kernel_config = False
        for build_obj in objset.foreach_type(oe.spdx30.build_Build):
            if getattr(build_obj, "build_buildType", "") == "https://openembedded.org/kernel-configuration":
                found_kernel_config = True
                self.assertTrue(
                    len(getattr(build_obj, "build_parameter", [])) > 0,
                    "Kernel configuration build_Build has no CONFIG_* entries"
                )
                break

        self.assertTrue(found_kernel_config, "Kernel configuration build_Build not found in SPDX output")

    def test_packageconfig_spdx(self):
        objset = self.check_recipe_spdx(
            "tar",
            "{DEPLOY_DIR_SPDX}/{SSTATE_PKGARCH}/builds/build-tar.spdx.json",
            extraconf="""\
                SPDX_INCLUDE_PACKAGECONFIG = "1"
                """,
        )

        found_entries = []
        for build_obj in objset.foreach_type(oe.spdx30.build_Build):
            for param in getattr(build_obj, "build_parameter", []):
                if param.key.startswith("PACKAGECONFIG:"):
                    found_entries.append((param.key, param.value))

        self.assertTrue(
            found_entries,
            "No PACKAGECONFIG entries found in SPDX output for 'tar'"
        )

        for key, value in found_entries:
            self.assertIn(
                value, ["enabled", "disabled"],
                f"Unexpected PACKAGECONFIG value '{value}' for {key}"
            )

    def test_download_location_defensive_handling(self):
        """Test that download_location handling is defensive.

        Verifies SPDX generation succeeds and external references are
        properly structured when download_location retrieval works.
        """
        objset = self.check_recipe_spdx(
            "m4",
            "{DEPLOY_DIR_SPDX}/{SSTATE_PKGARCH}/builds/build-m4.spdx.json",
        )

        found_external_refs = False
        for pkg in objset.foreach_type(oe.spdx30.software_Package):
            if pkg.externalRef:
                found_external_refs = True
                for ref in pkg.externalRef:
                    self.assertIsNotNone(ref.externalRefType)
                    self.assertIsNotNone(ref.locator)
                    self.assertGreater(len(ref.locator), 0, "Locator should have at least one entry")
                    for loc in ref.locator:
                        self.assertIsInstance(loc, str)
                break

        self.logger.info(
            f"External references {'found' if found_external_refs else 'not found'} "
            f"in SPDX output (defensive handling verified)"
        )

    def test_version_extraction_patterns(self):
        """Test that version extraction works for various package formats.

        Verifies that Git source downloads carry extracted versions and that
        the reported version strings are well-formed.
        """
        objset = self.check_recipe_spdx(
            "opkg-utils",
            "{DEPLOY_DIR_SPDX}/{SSTATE_PKGARCH}/builds/build-opkg-utils.spdx.json",
        )

        # Collect all packages with versions
        packages_with_versions = []
        for pkg in objset.foreach_type(oe.spdx30.software_Package):
            if pkg.software_packageVersion:
                packages_with_versions.append((pkg.name, pkg.software_packageVersion))

        self.assertGreater(
            len(packages_with_versions), 0,
            "Should find packages with extracted versions"
        )

        for name, version in packages_with_versions:
            self.assertRegex(
                version,
                r"^[0-9a-f]{40}$",
                f"Expected Git source version for {name} to be a full SHA-1",
            )

        self.logger.info(f"Found {len(packages_with_versions)} packages with versions")

        # Log some examples for debugging
        for name, version in packages_with_versions[:5]:
            self.logger.info(f"  {name}: {version}")

        # Verify that versions follow expected patterns
        for name, version in packages_with_versions:
            # Version should not be empty
            self.assertIsNotNone(version)
            self.assertNotEqual(version, "")

            # Version should contain digits
            self.assertRegex(
                version,
                r'\d',
                f"Version '{version}' for package '{name}' should contain digits"
            )
