#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

import os
import shutil
import unittest

from oeqa.core.utils.path import remove_safe
from oeqa.sdk.case import OESDKTestCase
from oeqa.utils.subprocesstweak import errors_have_output

errors_have_output()


class MaturinTest(OESDKTestCase):
    def setUp(self):
        if not (
            self.tc.hasHostPackage("nativesdk-python3-maturin")
            or self.tc.hasHostPackage("python3-maturin-native")
        ):
            raise unittest.SkipTest("No python3-maturin package in the SDK")

    def test_maturin_list_python(self):
        py_major = self._run("python3 -c 'import sys; print(sys.version_info.major)'")
        py_minor = self._run("python3 -c 'import sys; print(sys.version_info.minor)'")
        python_version = "%s.%s" % (py_major.strip(), py_minor.strip())
        cmd = "maturin list-python"
        output = self._run(cmd)
        self.assertRegex(output, r"^🐍 1 python interpreter found:\n")
        self.assertRegex(
            output,
            r" - CPython %s (.+)/usr/bin/python%s$" % (python_version, python_version),
        )


class MaturinDevelopTest(OESDKTestCase):
    @classmethod
    def setUpClass(self):
        targetdir = os.path.join(self.tc.sdk_dir, "guessing-game")
        try:
            shutil.rmtree(targetdir)
        except FileNotFoundError:
            pass
        shutil.copytree(
            os.path.join(self.tc.files_dir, "maturin/guessing-game"), targetdir
        )

    def setUp(self):
        machine = self.td.get("MACHINE")
        if not (
            self.tc.hasHostPackage("nativesdk-python3-maturin")
            or self.tc.hasHostPackage("python3-maturin-native")
        ):
            raise unittest.SkipTest("No python3-maturin package in the SDK")
        if not (
            self.tc.hasHostPackage("packagegroup-rust-cross-canadian-%s" % machine)
        ):
            raise unittest.SkipTest(
                "Testing 'maturin develop' requires Rust cross-canadian in the SDK"
            )

    def test_maturin_develop(self):
        """
        This test case requires:
          (1) that a .venv can been created.
          (2) a functional 'rustc' and 'cargo'
        """
        self._run("cd %s/guessing-game; python3 -m venv .venv" % self.tc.sdk_dir)
        cmd = "cd %s/guessing-game; maturin develop" % self.tc.sdk_dir
        output = self._run(cmd)
        self.assertRegex(output, r"🔗 Found pyo3 bindings with abi3 support for Python ≥ 3.8")
        self.assertRegex(output, r"🐍 Not using a specific python interpreter")
        self.assertRegex(output, r"📡 Using build options features from pyproject.toml")
        self.assertRegex(output, r"Compiling guessing-game v0.1.0")
        self.assertRegex(output, r"📦 Built wheel for abi3 Python ≥ 3.8")
        self.assertRegex(output, r"🛠 Installed guessing-game-0.1.0")
