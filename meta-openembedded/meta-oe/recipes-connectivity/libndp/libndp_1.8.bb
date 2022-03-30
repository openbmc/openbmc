SUMMARY = "Library for IPv6 Neighbor Discovery Protocol"
HOMEPAGE = "http://libndp.org/"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

SRC_URI = "git://github.com/jpirko/libndp;branch=master;protocol=https \
           "
# tag for v1.8
SRCREV = "009ce9cd9b950ffa1f4f94c9436027b936850d0c"
S = "${WORKDIR}/git"

inherit autotools
