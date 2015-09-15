# Local file checksum cache implementation
#
# Copyright (C) 2012 Intel Corporation
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License version 2 as
# published by the Free Software Foundation.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License along
# with this program; if not, write to the Free Software Foundation, Inc.,
# 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.

import os
import stat
import bb.utils
import logging
from bb.cache import MultiProcessCache

logger = logging.getLogger("BitBake.Cache")

try:
    import cPickle as pickle
except ImportError:
    import pickle
    logger.info("Importing cPickle failed. "
                "Falling back to a very slow implementation.")


# mtime cache (non-persistent)
# based upon the assumption that files do not change during bitbake run
class FileMtimeCache(object):
    cache = {}

    def cached_mtime(self, f):
        if f not in self.cache:
            self.cache[f] = os.stat(f)[stat.ST_MTIME]
        return self.cache[f]

    def cached_mtime_noerror(self, f):
        if f not in self.cache:
            try:
                self.cache[f] = os.stat(f)[stat.ST_MTIME]
            except OSError:
                return 0
        return self.cache[f]

    def update_mtime(self, f):
        self.cache[f] = os.stat(f)[stat.ST_MTIME]
        return self.cache[f]

    def clear(self):
        self.cache.clear()

# Checksum + mtime cache (persistent)
class FileChecksumCache(MultiProcessCache):
    cache_file_name = "local_file_checksum_cache.dat"
    CACHE_VERSION = 1

    def __init__(self):
        self.mtime_cache = FileMtimeCache()
        MultiProcessCache.__init__(self)

    def get_checksum(self, f):
        entry = self.cachedata[0].get(f)
        cmtime = self.mtime_cache.cached_mtime(f)
        if entry:
            (mtime, hashval) = entry
            if cmtime == mtime:
                return hashval
            else:
                bb.debug(2, "file %s changed mtime, recompute checksum" % f)

        hashval = bb.utils.md5_file(f)
        self.cachedata_extras[0][f] = (cmtime, hashval)
        return hashval

    def merge_data(self, source, dest):
        for h in source[0]:
            if h in dest:
                (smtime, _) = source[0][h]
                (dmtime, _) = dest[0][h]
                if smtime > dmtime:
                    dest[0][h] = source[0][h]
            else:
                dest[0][h] = source[0][h]
