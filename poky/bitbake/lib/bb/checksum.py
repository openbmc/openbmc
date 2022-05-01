# Local file checksum cache implementation
#
# Copyright (C) 2012 Intel Corporation
#
# SPDX-License-Identifier: GPL-2.0-only
#

import glob
import operator
import os
import stat
import bb.utils
import logging
import re
from bb.cache import MultiProcessCache

logger = logging.getLogger("BitBake.Cache")

filelist_regex = re.compile(r'(?:(?<=:True)|(?<=:False))\s+')

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
        f = os.path.normpath(f)
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

    def get_checksums(self, filelist, pn, localdirsexclude):
        """Get checksums for a list of files"""

        def checksum_file(f):
            try:
                checksum = self.get_checksum(f)
            except OSError as e:
                bb.warn("Unable to get checksum for %s SRC_URI entry %s: %s" % (pn, os.path.basename(f), e))
                return None
            return checksum

        #
        # Changing the format of file-checksums is problematic as both OE and Bitbake have
        # knowledge of them. We need to encode a new piece of data, the portion of the path
        # we care about from a checksum perspective. This means that files that change subdirectory
        # are tracked by the task hashes. To do this, we do something horrible and put a "/./" into
        # the path. The filesystem handles it but it gives us a marker to know which subsection
        # of the path to cache.
        #
        def checksum_dir(pth):
            # Handle directories recursively
            if pth == "/":
                bb.fatal("Refusing to checksum /")
            pth = pth.rstrip("/")
            dirchecksums = []
            for root, dirs, files in os.walk(pth, topdown=True):
                [dirs.remove(d) for d in list(dirs) if d in localdirsexclude]
                for name in files:
                    fullpth = os.path.join(root, name).replace(pth, os.path.join(pth, "."))
                    checksum = checksum_file(fullpth)
                    if checksum:
                        dirchecksums.append((fullpth, checksum))
            return dirchecksums

        checksums = []
        for pth in filelist_regex.split(filelist):
            if not pth:
                continue
            pth = pth.strip()
            if not pth:
                continue
            exist = pth.split(":")[1]
            if exist == "False":
                continue
            pth = pth.split(":")[0]
            if '*' in pth:
                # Handle globs
                for f in glob.glob(pth):
                    if os.path.isdir(f):
                        if not os.path.islink(f):
                            checksums.extend(checksum_dir(f))
                    else:
                        checksum = checksum_file(f)
                        if checksum:
                            checksums.append((f, checksum))
            elif os.path.isdir(pth):
                if not os.path.islink(pth):
                    checksums.extend(checksum_dir(pth))
            else:
                checksum = checksum_file(pth)
                if checksum:
                    checksums.append((pth, checksum))

        checksums.sort(key=operator.itemgetter(1))
        return checksums
