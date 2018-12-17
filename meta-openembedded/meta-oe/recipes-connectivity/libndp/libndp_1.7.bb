SUMMARY = "Library for IPv6 Neighbor Discovery Protocol"
HOMEPAGE = "http://libndp.org/"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

SRC_URI = "git://github.com/jpirko/libndp \
           "
# tag for v1.6
SRCREV = "96674e7d4f4d569c2c961e865cc16152dfab5f09"
S = "${WORKDIR}/git"

inherit autotools
