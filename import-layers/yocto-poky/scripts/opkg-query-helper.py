#!/usr/bin/env python3

# OpenEmbedded opkg query helper utility
#
# Written by: Paul Eggleton <paul.eggleton@linux.intel.com>
#
# Copyright 2012 Intel Corporation
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
#
#


import sys
import fileinput
import re

archmode = False
filemode = False
vermode = False

args = []
for arg in sys.argv[1:]:
    if arg == '-a':
        archmode = True
    elif arg == '-f':
        filemode = True
    elif arg == '-v':
        vermode = True
    else:
        args.append(arg)

# Regex for removing version specs after dependency items
verregex = re.compile(' \([=<>]* [^ )]*\)')

pkg = ""
ver = ""
for line in fileinput.input(args):
    line = line.rstrip()
    if ': ' in line:
        if line.startswith("Package:"):
            pkg = line.split(": ")[1]
            ver = ""
        else:
            if archmode:
                if line.startswith("Architecture:"):
                    arch = line.split(": ")[1]
                    print("%s %s" % (pkg,arch))
            elif filemode:
                if line.startswith("Version:"):
                    ver = line.split(": ")[1]
                elif line.startswith("Architecture:"):
                    arch = line.split(": ")[1]
                    print("%s %s_%s_%s.ipk %s" % (pkg,pkg,ver,arch,arch))
            elif vermode:
                if line.startswith("Version:"):
                    ver = line.split(": ")[1]
                elif line.startswith("Architecture:"):
                    arch = line.split(": ")[1]
                    print("%s %s %s" % (pkg,arch,ver))
            else:
                if line.startswith("Depends:"):
                    depval = line.split(": ")[1]
                    deps = depval.split(", ")
                    for dep in deps:
                        dep = verregex.sub('', dep)
                        print("%s|%s" % (pkg,dep))
                elif line.startswith("Recommends:"):
                    recval = line.split(": ")[1]
                    recs = recval.split(", ")
                    for rec in recs:
                        rec = verregex.sub('', rec)
                        print("%s|%s [REC]" % (pkg, rec))

