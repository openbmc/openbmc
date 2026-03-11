#
# Copyright BitBake Contributors
#
# SPDX-License-Identifier: GPL-2.0-only
#

import bb.compress._pipecompress
import shutil


def open(*args, **kwargs):
    return bb.compress._pipecompress.open_wrap(ZstdFile, *args, **kwargs)


class ZstdFile(bb.compress._pipecompress.PipeFile):
    def __init__(self, *args, num_threads=1, compresslevel=3, **kwargs):
        self.num_threads = num_threads
        self.compresslevel = compresslevel
        super().__init__(*args, **kwargs)

    def _get_zstd(self):
        if self.num_threads == 1 or not shutil.which("pzstd"):
            return ["zstd"]
        return ["pzstd", "-p", "%d" % self.num_threads]

    def get_compress(self):
        return self._get_zstd() + ["-c", "-%d" % self.compresslevel]

    def get_decompress(self):
        return self._get_zstd() + ["-d", "-c"]
