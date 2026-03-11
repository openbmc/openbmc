#!/usr/bin/env python3
#
# Copyright (C) 2012, 2018 Wind River Systems, Inc.
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
# Used for dumping the bb_cache.dat
#
import os
import sys
import argparse

# For importing bb.cache
sys.path.insert(0, os.path.join(os.path.abspath(os.path.dirname(sys.argv[0])), '../lib'))
from bb.cache import CoreRecipeInfo

import pickle

class DumpCache(object):
    def __init__(self):
        parser = argparse.ArgumentParser(
            description="bb_cache.dat's dumper",
            epilog="Use %(prog)s --help to get help")
        parser.add_argument("-r", "--recipe",
            help="specify the recipe, default: all recipes", action="store")
        parser.add_argument("-m", "--members",
            help = "specify the member, use comma as separator for multiple ones, default: all members", action="store", default="")
        parser.add_argument("-s", "--skip",
            help = "skip skipped recipes", action="store_true")
        parser.add_argument("cachefile",
            help = "specify bb_cache.dat", nargs = 1, action="store", default="")

        self.args = parser.parse_args()

    def main(self):
        with open(self.args.cachefile[0], "rb") as cachefile:
            pickled = pickle.Unpickler(cachefile)
            while True:
                try:
                    key = pickled.load()
                    val = pickled.load()
                except Exception:
                    break
                if isinstance(val, CoreRecipeInfo):
                    pn = val.pn

                    if self.args.recipe and self.args.recipe != pn:
                        continue

                    if self.args.skip and val.skipped:
                        continue

                    if self.args.members:
                        out = key
                        for member in self.args.members.split(','):
                            out += ": %s" % val.__dict__.get(member)
                        print("%s" % out)
                    else:
                        print("%s: %s" % (key, val.__dict__))
                elif not self.args.recipe:
                    print("%s %s" % (key, val))

if __name__ == "__main__":
    try:
        dump = DumpCache()
        ret = dump.main()
    except Exception as esc:
        ret = 1
        import traceback
        traceback.print_exc()
    sys.exit(ret)
