#
# Copyright BitBake Contributors
#
# SPDX-License-Identifier: GPL-2.0-only
#

from pathlib import Path
import bb.compress.lz4
import bb.compress.zstd
import contextlib
import os
import shutil
import tempfile
import unittest
import subprocess


class CompressionTests(object):
    def setUp(self):
        self._t = tempfile.TemporaryDirectory()
        self.tmpdir = Path(self._t.name)
        self.addCleanup(self._t.cleanup)

    def _file_helper(self, mode_suffix, data):
        tmp_file = self.tmpdir / "compressed"

        with self.do_open(tmp_file, mode="w" + mode_suffix) as f:
            f.write(data)

        with self.do_open(tmp_file, mode="r" + mode_suffix) as f:
            read_data = f.read()

        self.assertEqual(read_data, data)

    def test_text_file(self):
        self._file_helper("t", "Hello")

    def test_binary_file(self):
        self._file_helper("b", "Hello".encode("utf-8"))

    def _pipe_helper(self, mode_suffix, data):
        rfd, wfd = os.pipe()
        with open(rfd, "rb") as r, open(wfd, "wb") as w:
            with self.do_open(r, mode="r" + mode_suffix) as decompress:
                with self.do_open(w, mode="w" + mode_suffix) as compress:
                    compress.write(data)
                read_data = decompress.read()

        self.assertEqual(read_data, data)

    def test_text_pipe(self):
        self._pipe_helper("t", "Hello")

    def test_binary_pipe(self):
        self._pipe_helper("b", "Hello".encode("utf-8"))

    def test_bad_decompress(self):
        tmp_file = self.tmpdir / "compressed"
        with tmp_file.open("wb") as f:
            f.write(b"\x00")

        with self.assertRaises(OSError):
            with self.do_open(tmp_file, mode="rb", stderr=subprocess.DEVNULL) as f:
                data = f.read()


class LZ4Tests(CompressionTests, unittest.TestCase):
    def setUp(self):
        if shutil.which("lz4") is None:
            self.skipTest("'lz4' not found")
        super().setUp()

    @contextlib.contextmanager
    def do_open(self, *args, **kwargs):
        with bb.compress.lz4.open(*args, **kwargs) as f:
            yield f


class ZStdTests(CompressionTests, unittest.TestCase):
    def setUp(self):
        if shutil.which("zstd") is None:
            self.skipTest("'zstd' not found")
        super().setUp()

    @contextlib.contextmanager
    def do_open(self, *args, **kwargs):
        with bb.compress.zstd.open(*args, **kwargs) as f:
            yield f


class PZStdTests(CompressionTests, unittest.TestCase):
    def setUp(self):
        if shutil.which("pzstd") is None:
            self.skipTest("'pzstd' not found")
        super().setUp()

    @contextlib.contextmanager
    def do_open(self, *args, **kwargs):
        with bb.compress.zstd.open(*args, num_threads=2, **kwargs) as f:
            yield f
