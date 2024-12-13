#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

import json
import os
import textwrap
from pathlib import Path
from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import bitbake, get_bb_var, get_bb_vars, runCmd


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
        machine_var = get_bb_var("MACHINE")
        spdx_version = get_bb_var("SPDX_VERSION")
        # qemux86-64 creates the directory qemux86_64
        machine_dir = machine_var.replace("-", "_")

        full_file_path = os.path.join(
            deploy_dir, "spdx", spdx_version, machine_dir, high_level_dir, spdx_file
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


class SPDX3CheckBase(object):
    """
    Base class for checking SPDX 3 based tests
    """

    def check_spdx_file(self, filename):
        import oe.spdx30

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
            {extraconf}
            """
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
                    "TOOLCHAIN_OUTPUTNAME",
                ],
                target_name,
            )
        )

        return self.check_spdx_file(filename)

    def check_objset_missing_ids(self, objset):
        if objset.missing_ids:
            self.assertTrue(
                False,
                "The following SPDXIDs are unresolved:\n  "
                + "\n  ".join(objset.missing_ids),
            )


class SPDX30Check(SPDX3CheckBase, OESelftestTestCase):
    SPDX_CLASS = "create-spdx-3.0"

    def test_base_files(self):
        self.check_recipe_spdx(
            "base-files",
            "{DEPLOY_DIR_SPDX}/{MACHINE_ARCH}/packages/base-files.spdx.json",
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
            extraconf=textwrap.dedent(
                """\
                TCLIBC = "baremetal"
                """
            ),
        )

        # Document should be fully linked
        self.check_objset_missing_ids(objset)
