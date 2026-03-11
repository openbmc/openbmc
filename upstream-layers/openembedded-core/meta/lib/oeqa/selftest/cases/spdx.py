#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

import json
import os
import textwrap
import hashlib
from pathlib import Path
from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import bitbake, get_bb_var, get_bb_vars, runCmd
import oe.spdx30


class SPDX22Check(OESelftestTestCase):
    @classmethod
    def setUpClass(cls):
        super().setUpClass()
        bitbake("python3-spdx-tools-native")
        bitbake("-c addto_recipe_sysroot python3-spdx-tools-native")

    def check_recipe_spdx(self, high_level_dir, spdx_file, target_name):
        config = textwrap.dedent(
            """\
            INHERIT:remove = "create-spdx"
            INHERIT += "create-spdx-2.2"
            """
        )
        self.write_config(config)

        deploy_dir = get_bb_var("DEPLOY_DIR")
        arch_dir = get_bb_var("PACKAGE_ARCH", target_name)
        spdx_version = get_bb_var("SPDX_VERSION")
        # qemux86-64 creates the directory qemux86_64
        #arch_dir = arch_var.replace("-", "_")

        full_file_path = os.path.join(
            deploy_dir, "spdx", spdx_version, arch_dir, high_level_dir, spdx_file
        )

        try:
            os.remove(full_file_path)
        except FileNotFoundError:
            pass

        bitbake("%s -c create_spdx" % target_name)

        def check_spdx_json(filename):
            with open(filename) as f:
                report = json.load(f)
                self.assertNotEqual(report, None)
                self.assertNotEqual(report["SPDXID"], None)

            python = os.path.join(
                get_bb_var("STAGING_BINDIR", "python3-spdx-tools-native"),
                "nativepython3",
            )
            validator = os.path.join(
                get_bb_var("STAGING_BINDIR", "python3-spdx-tools-native"), "pyspdxtools"
            )
            result = runCmd("{} {} -i {}".format(python, validator, filename))

        self.assertExists(full_file_path)
        result = check_spdx_json(full_file_path)

    def test_spdx_base_files(self):
        self.check_recipe_spdx("packages", "base-files.spdx.json", "base-files")

    def test_spdx_tar(self):
        self.check_recipe_spdx("packages", "tar.spdx.json", "tar")


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
        config = (
            textwrap.dedent(
                f"""\
                INHERIT:remove = "create-spdx"
                INHERIT += "{self.SPDX_CLASS}"
                """
            )
            + textwrap.dedent(extraconf)
        )

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
            "{DEPLOY_DIR_SPDX}/{MACHINE_ARCH}/packages/package-base-files.spdx.json",
        )

    def test_gcc_include_source(self):
        objset = self.check_recipe_spdx(
            "gcc",
            "{DEPLOY_DIR_SPDX}/{SSTATE_PKGARCH}/recipes/recipe-gcc.spdx.json",
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
                    f"The spdxId of {filename} in recipe-gcc.spdx.json is {software_file.spdxId}"
                )
                break

        self.assertTrue(
            found, f"Not found source file {filename} in recipe-gcc.spdx.json\n"
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
