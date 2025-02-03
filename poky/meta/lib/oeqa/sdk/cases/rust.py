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

class RustCompileTest(OESDKTestCase):
    td_vars = ['MACHINE']

    @classmethod
    def setUpClass(self):
        targetdir = os.path.join(self.tc.sdk_dir, "hello")
        try:
            shutil.rmtree(targetdir)
        except FileNotFoundError:
            pass
        shutil.copytree(os.path.join(self.tc.sdk_files_dir, "rust/hello"), targetdir)

    def setUp(self):
        machine = self.td.get("MACHINE")
        if not self.tc.hasHostPackage("packagegroup-rust-cross-canadian-%s" % machine):
            raise unittest.SkipTest("RustCompileTest class: SDK doesn't contain a Rust cross-canadian toolchain")

    def test_cargo_build(self):
        self._run('cd %s/hello; cargo add zstd' % (self.tc.sdk_dir))
        self._run('cd %s/hello; cargo build' % self.tc.sdk_dir)

class RustHostCompileTest(OESDKTestCase):
    td_vars = ['MACHINE', 'SDK_SYS']

    @classmethod
    def setUpClass(self):
        targetdir = os.path.join(self.tc.sdk_dir, "hello")
        try:
            shutil.rmtree(targetdir)
        except FileNotFoundError:
            pass
        shutil.copytree(os.path.join(self.tc.sdk_files_dir, "rust/hello"), targetdir)

    def setUp(self):
        machine = self.td.get("MACHINE")
        if not self.tc.hasHostPackage("packagegroup-rust-cross-canadian-%s" % machine):
            raise unittest.SkipTest("RustCompileTest class: SDK doesn't contain a Rust cross-canadian toolchain")

    def test_cargo_build(self):
        sdksys = self.td.get("SDK_SYS")
        self._run('cd %s/hello; cargo add zstd' % (self.tc.sdk_dir))
        self._run('cd %s/hello; cargo build --target %s-gnu' % (self.tc.sdk_dir, sdksys))
        self._run('cd %s/hello; cargo run --target %s-gnu' % (self.tc.sdk_dir, sdksys))
