#!/bin/sh
#
# Copyright (c) 2017, Intel Corporation.
# All rights reserved.
#
# This program is free software;  you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation; either version 2 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY;  without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See
# the GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program;  if not, write to the Free Software
# Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
#
# Description: creates a new set of kernel templates based on version
#

set -o nounset
set -o errexit

if [ $# -ne 4 ]; then
    cat << EOF
usage: $0 from_mayor from_minor to_mayor to_minor
EOF
    exit 1
else
    fma=$1 # from mayor
    fmi=$2 # from minor
    tma=$3 # to mayor
    tmi=$4 # to minor
fi

poky=$(readlink -e $(dirname $(dirname $(dirname $0))))
arch=$poky/scripts/lib/bsp/substrate/target/arch


# copy/rename templates
for from in $(ls -1 $arch/*/recipes-kernel/linux/linux-yocto*_$fma\.$fmi.bbappend)
do
    to=$(echo $from | sed s/$fma\.$fmi/$tma\.$tmi/)
    cp $from $to
done

# replace versions string inside new templates
for bbappend in $(ls -1 $arch/*/recipes-kernel/linux/linux-yocto*_$tma\.$tmi.bbappend)
do
    sed -i 1s/$fma\.$fmi/$tma\.$tmi/ $bbappend
    sed -i \$s/$fma\.$fmi/$tma\.$tmi/ $bbappend
done

# update the noinstall files
for noinstall in $(ls -1 $arch/*/recipes-kernel/linux/kernel-list.noinstall)
do
    sed -i s/$fma\.$fmi/$tma\.$tmi/g $noinstall;
done
