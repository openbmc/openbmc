#!/usr/bin/env python3
# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
#
# Copyright (C) 2012 Wind River Systems, Inc.
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
# This is used for dumping the bb_cache.dat, the output format is:
# recipe_path PN PV PACKAGES
#
import os
import sys
import warnings

# For importing bb.cache
sys.path.insert(0, os.path.join(os.path.abspath(os.path.dirname(sys.argv[0])), '../lib'))
from bb.cache import CoreRecipeInfo

import pickle as pickle

def main(argv=None):
    """
    Get the mapping for the target recipe.
    """
    if len(argv) != 1:
        print("Error, need one argument!", file=sys.stderr)
        return 2

    cachefile = argv[0]

    with open(cachefile, "rb") as cachefile:
        pickled = pickle.Unpickler(cachefile)
        while cachefile:
            try:
                key = pickled.load()
                val = pickled.load()
            except Exception:
                break
            if isinstance(val, CoreRecipeInfo) and (not val.skipped):
                pn = val.pn
                # Filter out the native recipes.
                if key.startswith('virtual:native:') or pn.endswith("-native"):
                    continue

                # 1.0 is the default version for a no PV recipe.
                if "pv" in val.__dict__:
                    pv = val.pv
                else:
                    pv = "1.0"

                print("%s %s %s %s" % (key, pn, pv, ' '.join(val.packages)))

if __name__ == "__main__":
    sys.exit(main(sys.argv[1:]))

