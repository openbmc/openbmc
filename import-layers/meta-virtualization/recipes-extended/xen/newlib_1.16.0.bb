# Copyright (C) 2017 Kurt Bodiker <kurt.bodiker@braintrust-us.com>
# Released under the MIT license (see COPYING.MIT for the terms)

DESCRIPTION = "Newlib is a C library intended for use on embedded systems."
HOMEPAGE = "http://sourceware.org/newlib"
LICENSE = "GPLv2 & LGPLv3 & GPLv3 & LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING.NEWLIB;md5=950f50b290e8fcf7a2d3fff61775de9b"

# this is the hash of version tag 1_16_0
SRCREV_newlib = "07b4b67a88f386ce4716a14e0ff2c2bce992b985"
SRC_URI = "\
    git://sourceware.org/git/newlib-cygwin.git;protocol=git;nobranch=1;destsuffix=newlib;name=newlib \
    file://newlib.patch \
    file://newlib-chk.patch \
    file://newlib-stdint-size_max-fix-from-1.17.0.patch \
"

S="${WORKDIR}/newlib"
B="${WORKDIR}/build"

require newlib.inc
