#!/bin/bash
#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: GPL-2.0-only
#
# This is an example script to be used in conjunction with test_build_time.sh

if [ "$TEST_BUILDDIR" = "" ] ; then
    echo "TEST_BUILDDIR is not set"
    exit 1
fi

buildsubdir=`basename $TEST_BUILDDIR`
if [ ! -d $buildsubdir ] ; then
    echo "Unable to find build subdir $buildsubdir in current directory"
    exit 1
fi

if [ -f oe-init-build-env ] ; then
    . ./oe-init-build-env $buildsubdir
elif [ -f poky-init-build-env ] ; then
    . ./poky-init-build-env $buildsubdir
else
    echo "Unable to find build environment setup script"
    exit 1
fi

if [ -f ../meta/recipes-sato/images/core-image-sato.bb ] ; then
    target="core-image-sato"
else
    target="poky-image-sato"
fi

echo "Build started at `date "+%Y-%m-%d %H:%M:%S"`"
echo "bitbake $target"
bitbake $target
ret=$?
echo "Build finished at `date "+%Y-%m-%d %H:%M:%S"`"
exit $ret

