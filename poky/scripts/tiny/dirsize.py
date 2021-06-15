#!/usr/bin/env python3
#
# Copyright (c) 2011, Intel Corporation.
#
# SPDX-License-Identifier: GPL-2.0-or-later
#
# Display details of the root filesystem size, broken up by directory.
# Allows for limiting by size to focus on the larger files.
#
# Author: Darren Hart <dvhart@linux.intel.com>
#

import os
import sys
import stat

class Record:
    def create(path):
        r = Record(path)

        s = os.lstat(path)
        if stat.S_ISDIR(s.st_mode):
            for p in os.listdir(path):
                pathname = path + "/" + p
                ss = os.lstat(pathname)
                if not stat.S_ISLNK(ss.st_mode):
                    r.records.append(Record.create(pathname))
                    r.size += r.records[-1].size
            r.records.sort(reverse=True)
        else:
            r.size = os.lstat(path).st_size

        return r
    create = staticmethod(create)

    def __init__(self, path):
        self.path = path
        self.size = 0
        self.records = []

    def __lt__(this, that):
        if that is None:
            return False
        if not isinstance(that, Record):
            raise TypeError
        if len(this.records) > 0 and len(that.records) == 0:
            return False
        if this.size > that.size:
            return False
        return True

    def show(self, minsize):
        total = 0
        if self.size <= minsize:
            return 0
        print("%10d %s" % (self.size, self.path))
        for r in self.records:
            total += r.show(minsize)
        if len(self.records) == 0:
            total = self.size
        return total


def main():
    minsize = 0
    if len(sys.argv) == 2:
        minsize = int(sys.argv[1])
    rootfs = Record.create(".")
    total = rootfs.show(minsize)
    print("Displayed %d/%d bytes (%.2f%%)" % \
          (total, rootfs.size, 100 * float(total) / rootfs.size))


if __name__ == "__main__":
    main()
