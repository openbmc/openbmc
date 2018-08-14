# Path utility functions for OE python scripts
#
# Copyright (C) 2012-2014 Intel Corporation
# Copyright (C) 2011 Mentor Graphics Corporation
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
