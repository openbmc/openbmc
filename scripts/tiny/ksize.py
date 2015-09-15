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
# Display details of the kernel build size, broken up by built-in.o. Sort
# the objects by size. Run from the top level kernel build directory.
#
# Author: Darren Hart <dvhart@linux.intel.com>
#

import sys
import getopt
import os
from subprocess import *
from string import join


def usage():
    prog = os.path.basename(sys.argv[0])
    print 'Usage: %s [OPTION]...' % (prog)
    print '  -d,                 display an additional level of drivers detail'
    print '  -h, --help          display this help and exit'
    print ''
    print 'Run %s from the top-level Linux kernel build directory.' % (prog)


class Sizes:
    def __init__(self, glob):
        self.title = glob
        p = Popen("size -t " + glob, shell=True, stdout=PIPE, stderr=PIPE)
        output = p.communicate()[0].splitlines()
        if len(output) > 2:
            sizes = output[-1].split()[0:4]
            self.text = int(sizes[0])
            self.data = int(sizes[1])
            self.bss = int(sizes[2])
            self.total = int(sizes[3])
        else:
            self.text = self.data = self.bss = self.total = 0

    def show(self, indent=""):
        print "%-32s %10d | %10d %10d %10d" % \
              (indent+self.title, self.total, self.text, self.data, self.bss)


class Report:
    def create(filename, title, subglob=None):
        r = Report(filename, title)
        path = os.path.dirname(filename)

        p = Popen("ls " + path + "/*.o | grep -v built-in.o",
                  shell=True, stdout=PIPE, stderr=PIPE)
        glob = join(p.communicate()[0].splitlines())
        oreport = Report(glob, path + "/*.o")
        oreport.sizes.title = path + "/*.o"
        r.parts.append(oreport)

        if subglob:
            p = Popen("ls " + subglob, shell=True, stdout=PIPE, stderr=PIPE)
            for f in p.communicate()[0].splitlines():
                path = os.path.dirname(f)
                r.parts.append(Report.create(f, path, path + "/*/built-in.o"))
            r.parts.sort(reverse=True)

        for b in r.parts:
            r.totals["total"] += b.sizes.total
            r.totals["text"] += b.sizes.text
            r.totals["data"] += b.sizes.data
            r.totals["bss"] += b.sizes.bss

        r.deltas["total"] = r.sizes.total - r.totals["total"]
        r.deltas["text"] = r.sizes.text - r.totals["text"]
        r.deltas["data"] = r.sizes.data - r.totals["data"]
        r.deltas["bss"] = r.sizes.bss - r.totals["bss"]
        return r
    create = staticmethod(create)

    def __init__(self, glob, title):
        self.glob = glob
        self.title = title
        self.sizes = Sizes(glob)
        self.parts = []
        self.totals = {"total":0, "text":0, "data":0, "bss":0}
        self.deltas = {"total":0, "text":0, "data":0, "bss":0}

    def show(self, indent=""):
        rule = str.ljust(indent, 80, '-')
        print "%-32s %10s | %10s %10s %10s" % \
              (indent+self.title, "total", "text", "data", "bss")
        print rule
        self.sizes.show(indent)
        print rule
        for p in self.parts:
            if p.sizes.total > 0:
                p.sizes.show(indent)
        print rule
        print "%-32s %10d | %10d %10d %10d" % \
              (indent+"sum", self.totals["total"], self.totals["text"],
               self.totals["data"], self.totals["bss"])
        print "%-32s %10d | %10d %10d %10d" % \
              (indent+"delta", self.deltas["total"], self.deltas["text"],
               self.deltas["data"], self.deltas["bss"])
        print "\n"

    def __cmp__(this, that):
        if that is None:
            return 1
        if not isinstance(that, Report):
            raise TypeError
        if this.sizes.total < that.sizes.total:
            return -1
        if this.sizes.total > that.sizes.total:
            return 1
        return 0


def main():
    try:
        opts, args = getopt.getopt(sys.argv[1:], "dh", ["help"])
    except getopt.GetoptError, err:
        print '%s' % str(err)
        usage()
        sys.exit(2)

    driver_detail = False
    for o, a in opts:
        if o == '-d':
            driver_detail = True
        elif o in ('-h', '--help'):
            usage()
            sys.exit(0)
        else:
            assert False, "unhandled option"

    glob = "arch/*/built-in.o */built-in.o"
    vmlinux = Report.create("vmlinux",  "Linux Kernel", glob)

    vmlinux.show()
    for b in vmlinux.parts:
        if b.totals["total"] > 0 and len(b.parts) > 1:
            b.show()
        if b.title == "drivers" and driver_detail:
            for d in b.parts:
                if d.totals["total"] > 0 and len(d.parts) > 1:
                    d.show("    ")


if __name__ == "__main__":
    main()
