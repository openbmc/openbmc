#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

import os
import shutil
import unittest

from oeqa.sdk.case import OESDKTestCase
from oeqa.utils.subprocesstweak import errors_have_output

errors_have_output()


class MaturinTest(OESDKTestCase):
    def setUp(self):
        self.ensure_host_package("python3-maturin")

    def test_maturin_list_python(self):
        out = self._run(r"""python3 -c 'import sys; print(f"{sys.executable}\n{sys.version_info.major}.{sys.version_info.minor}")'""")
        executable, version = out.splitlines()

        output = self._run("maturin list-python")
        # The output looks like this:
        # - CPython 3.13 at /usr/bin/python3
        # We don't want to assume CPython so just check for the version and path.
        expected = f"{version} at {executable}"
        self.assertIn(expected, output)

class MaturinDevelopTest(OESDKTestCase):
    def setUp(self):
        machine = self.td.get("MACHINE")
        self.ensure_host_package("python3-maturin")

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
        targetdir = os.path.join(self.tc.sdk_dir, "guessing-game")
        try:
            shutil.rmtree(targetdir)
        except FileNotFoundError:
            pass
        shutil.copytree(
            os.path.join(self.tc.files_dir, "maturin/guessing-game"), targetdir
        )

        self._run("cd %s; python3 -m venv .venv" % targetdir)
        output = self._run("cd %s; maturin develop" % targetdir)
        self.assertRegex(output, r"🔗 Found pyo3 bindings with abi3 support for Python ≥ 3.8")
        self.assertRegex(output, r"🐍 Not using a specific python interpreter")
        self.assertRegex(output, r"📡 Using build options features from pyproject.toml")
        self.assertRegex(output, r"Compiling guessing-game v0.1.0")
        self.assertRegex(output, r"📦 Built wheel for abi3 Python ≥ 3.8")
        self.assertRegex(output, r"🛠 Installed guessing-game-0.1.0")
