#
# Chris Lumens <clumens@redhat.com>
#
# Copyright 2007, 2008, 2009, 2010 Red Hat, Inc.
#
# This copyrighted material is made available to anyone wishing to use, modify,
# copy, or redistribute it subject to the terms and conditions of the GNU
# General Public License v.2.  This program is distributed in the hope that it
# will be useful, but WITHOUT ANY WARRANTY expressed or implied, including the
# implied warranties of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
# See the GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License along with
# this program; if not, write to the Free Software Foundation, Inc., 51
# Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  Any Red Hat
# trademarks that are incorporated in the source code or documentation are not
# subject to the GNU General Public License and may only be used or replicated
# with the express permission of Red Hat, Inc. 
#
from pykickstart.version import *
from pykickstart.commands import *

# This map is keyed on kickstart syntax version as provided by
# pykickstart.version.  Within each sub-dict is a mapping from command name
# to the class that handles it.  This is an onto mapping - that is, multiple
# command names can map to the same class.  However, the Handler will ensure
# that only one instance of each class ever exists.
commandMap = {
    # based on f15
    F16: {
        "bootloader": bootloader.F15_Bootloader,
        "part": partition.F14_Partition,
        "partition": partition.F14_Partition,
    },
}

# This map is keyed on kickstart syntax version as provided by
# pykickstart.version.  Within each sub-dict is a mapping from a data object
# name to the class that provides it.  This is a bijective mapping - that is,
# each name maps to exactly one data class and all data classes have a name.
# More than one instance of each class is allowed to exist, however.
dataMap = {
    F16: {
        "PartData": partition.F14_PartData,
    },
}
