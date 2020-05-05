#!/bin/bash
#
# Copyright 2014-present Facebook. All Rights Reserved.
#
# This program file is free software; you can redistribute it and/or modify it
# under the terms of the GNU General Public License as published by the
# Free Software Foundation; version 2 of the License.
#
# This program is distributed in the hope that it will be useful, but WITHOUT
# ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
# FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
# for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program in a file named COPYING; if not, write to the
# Free Software Foundation, Inc.,
# 51 Franklin Street, Fifth Floor,
# Boston, MA 02110-1301 USA
#

usage() {
    echo "Usage: $1 <slot#> <on | off>"
    exit -1
}

. /usr/sbin/ast-functions

PATH=/sbin:/bin:/usr/sbin:/usr/bin

set -e

if [ $# != 2 ]; then
    usage $0
fi

# Slot#1: GPIOM0(96),Slot#2: GPIOM1(97),Slot#3: GPIOM2(98),Slot#4: GPIOM3(99)
if [ $1 = "1" ]; then
    gpio=M0
elif [ $1 = "2" ]; then
    gpio=M1
elif [ $1 = "3" ]; then
    gpio=M2
elif [ $1 = "4" ]; then
    gpio=M3
else
    usage $0
fi


if [ $2 = "on" ]; then
    val=1
elif [ $2 = "off" ]; then
    val=0
else
    usage $0
fi

gpio_set $gpio $val
