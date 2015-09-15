#
# BitBake UI Utils 
#
# Copyright (C) 2012 Intel Corporation
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

# This utility method looks for xterm or vte and return the 
# frist to exist, currently we are keeping this simple, but 
# we will likely move the oe.terminal implementation into 
# bitbake which will allow more flexibility.

import os
import bb

def which_terminal():
    term = bb.utils.which(os.environ["PATH"], "xterm")
    if term:
        return term + " -e "
    term = bb.utils.which(os.environ["PATH"], "vte")
    if term:
        return term + " -c "
    return None
