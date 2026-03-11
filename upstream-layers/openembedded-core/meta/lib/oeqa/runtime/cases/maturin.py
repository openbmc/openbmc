#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

import os

from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.runtime.decorator.package import OEHasPackage


class MaturinTest(OERuntimeTestCase):
    @OETestDepends(['ssh.SSHTest.test_ssh', 'python.PythonTest.test_python3'])
    @OEHasPackage(['python3-maturin'])
    def test_maturin_list_python(self):
        status, output = self.target.run("maturin list-python")
        self.assertEqual(status, 0)
        _, py_major = self.target.run("python3 -c 'import sys; print(sys.version_info.major)'")
        _, py_minor = self.target.run("python3 -c 'import sys; print(sys.version_info.minor)'")
        python_version = "%s.%s" % (py_major, py_minor)
        self.assertEqual(output, "🐍 1 python interpreter found:\n"
                                 " - CPython %s at /usr/bin/python%s" % (python_version, python_version))


class MaturinDevelopTest(OERuntimeTestCase):
    @classmethod
    def setUp(cls):
        dst = '/tmp'
        src = os.path.join(cls.tc.files_dir, "maturin/guessing-game")
        cls.tc.target.copyTo(src, dst)

    @classmethod
    def tearDown(cls):
        cls.tc.target.run('rm -rf %s' %  '/tmp/guessing-game/target')

    @OETestDepends(['ssh.SSHTest.test_ssh', 'python.PythonTest.test_python3'])
    @OEHasPackage(['python3-maturin'])
    def test_maturin_develop(self):
        """
        This test case requires:
          (1) that a .venv can been created.
          (2) DNS nameserver to resolve crate URIs for fetching
          (3) a functional 'rustc' and 'cargo'
        """
        targetdir = os.path.join("/tmp", "guessing-game")
        self.target.run("cd %s; python3 -m venv .venv" % targetdir)
        self.target.run("echo 'nameserver 8.8.8.8' > /etc/resolv.conf")
        cmd = "cd %s; maturin develop" % targetdir
        status, output = self.target.run(cmd)
        self.assertRegex(output, r"🔗 Found pyo3 bindings with abi3 support for Python ≥ 3.8")
        self.assertRegex(output, r"🐍 Not using a specific python interpreter")
        self.assertRegex(output, r"📡 Using build options features from pyproject.toml")
        self.assertRegex(output, r"Compiling guessing-game v0.1.0")
        self.assertRegex(output, r"📦 Built wheel for abi3 Python ≥ 3.8")
        self.assertRegex(output, r"🛠 Installed guessing-game-0.1.0")
        self.assertEqual(status, 0)
