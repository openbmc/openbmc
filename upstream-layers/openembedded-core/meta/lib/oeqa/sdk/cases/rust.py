#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

import os
import shutil
import unittest
import json
import subprocess
import re


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
        result_env = self._run("echo $CARGO_BUILD_TARGET")
        rust_target_sys = result_env.strip()
        result = self._run(
            "cd %s/hello; cargo build --message-format=json-render-diagnostics"
            % self.tc.sdk_dir
        )

        executable_path = None
        for line in result.splitlines():
            try:
                msg = json.loads(line)
            except json.JSONDecodeError:
                continue

            # Cargo emits multiple JSON messages; we want the executable
            if isinstance(msg, dict) and msg.get("executable"):
                executable_path = msg["executable"]

        parts = executable_path.split(os.sep)
        target_index = parts.index("target")
        path_component = parts[target_index + 1]
        if path_component in ("debug", "release"):
            file_output = subprocess.check_output(["file", executable_path]).decode().strip()
            match = re.search(r"/sysroots/([^/]+)/lib/", file_output)
            target_triple = match.group(1) if match else None
        else:
            target_triple = path_component

        self.assertTrue(
            rust_target_sys == target_triple,
            f"Cargo built natively for the nativesdk host instead of '{rust_target_sys}.\n"
            f"Cargo build needs to build for target by default not nativesdk_host"
        )
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
