#!/bin/sh
# This script is intended to be used sorely by overlayfs-create-dirs.service
# Usage: overlayfs-create-dirs.sh <LOWERDIR> <DATA_MOUNT_POINT>

lowerdir=$1
datamountpoint=$2
mkdir -p ${datamountpoint}/upper${lowerdir}
mkdir -p ${datamountpoint}/workdir${lowerdir}
if [ -d "$lowerdir" ]; then
    chown $(stat -c "%U:%G" ${lowerdir}) ${datamountpoint}/upper${lowerdir}
fi
