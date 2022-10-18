#
# Copyright BitBake Contributors
#
# SPDX-License-Identifier: GPL-2.0-only
#

import bb.compress._pipecompress


def open(*args, **kwargs):
    return bb.compress._pipecompress.open_wrap(LZ4File, *args, **kwargs)


class LZ4File(bb.compress._pipecompress.PipeFile):
    def get_compress(self):
        return ["lz4c", "-z", "-c"]

    def get_decompress(self):
        return ["lz4c", "-d", "-c"]
