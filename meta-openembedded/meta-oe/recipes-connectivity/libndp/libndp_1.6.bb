SUMMARY = "Library for IPv6 Neighbor Discovery Protocol"
HOMEPAGE = "http://libndp.org/"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

SRC_URI = "git://github.com/jpirko/libndp \
           file://0001-include-sys-select.h-for-fd_-definitions.patch \
           "
# tag for v1.6
SRCREV = "2f721c4ff519f38f46695a60d9f9d88f35bf3c1d"
S = "${WORKDIR}/git"

inherit autotools
