# Path utility functions for OE python scripts
#
# Copyright (C) 2012-2014 Intel Corporation
# Copyright (C) 2011 Mentor Graphics Corporation
#
# SPDX-License-Identifier: GPL-2.0-only
#

import sys
import os
import os.path

def add_oe_lib_path():
    basepath = os.path.abspath(os.path.dirname(__file__) + '/../..')
    newpath = basepath + '/meta/lib'
    sys.path.insert(0, newpath)

def add_bitbake_lib_path():
    basepath = os.path.abspath(os.path.dirname(__file__) + '/../..')
    bitbakepath = None
    if os.path.exists(basepath + '/bitbake/lib/bb'):
        bitbakepath = basepath + '/bitbake'
    else:
        # look for bitbake/bin dir in PATH
        for pth in os.environ['PATH'].split(':'):
            if os.path.exists(os.path.join(pth, '../lib/bb')):
                bitbakepath = os.path.abspath(os.path.join(pth, '..'))
                break

    if bitbakepath:
        sys.path.insert(0, bitbakepath + '/lib')
    return bitbakepath
