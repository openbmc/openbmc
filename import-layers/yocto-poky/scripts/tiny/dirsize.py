#!/usr/bin/env python
#
# Copyright (c) 2011, Intel Corporation.
# All rights reserved.
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation; either version 2 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
#
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

    def __cmp__(this, that):
        if that is None:
            return 1
        if not isinstance(that, Record):
            raise TypeError
        if len(this.records) > 0 and len(that.records) == 0:
            return -1
        if len(this.records) == 0 and len(that.records) > 0:
            return 1
        if this.size < that.size:
            return -1
        if this.size > that.size:
            return 1
        return 0

    def show(self, minsize):
        total = 0
        if self.size <= minsize:
            return 0
        print "%10d %s" % (self.size, self.path)
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
    print "Displayed %d/%d bytes (%.2f%%)" % \
          (total, rootfs.size, 100 * float(total) / rootfs.size)


if __name__ == "__main__":
    main()
