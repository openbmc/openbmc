#! /usr/bin/env python3
#
# SPDX-License-Identifier: MIT
#
# Copyright 2019 by Garmin Ltd. or its subsidiaries
#
# A script to reformat python sysconfig

import sys
import pprint
l = {}
g = {}
with open(sys.argv[1], 'r') as f:
    exec(f.read(), g, l)

with open(sys.argv[1], 'w') as f:
    for k in sorted(l.keys()):
        f.write('%s = ' % k)
        pprint.pprint(l[k], stream=f, width=sys.maxsize)
        f.write('\n')

