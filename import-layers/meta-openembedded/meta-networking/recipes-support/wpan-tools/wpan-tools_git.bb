SUMMARY = "Userspace tools for Linux IEEE 802.15.4 stack"
HOMEPAGE = "http://wpan.cakelab.org/releases/"
DESCRIPTION = "This is a set of utils to manage the Linux WPAN stack via \
netlink interface. This requires recent kernel with nl802154 interface."

LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://COPYING;md5=4cfd939b1d7e6aba9fcefb7f6e2fd45d"

DEPENDS = "libnl"

PV = "0.8+git${SRCPV}"
SRC_URI = "git://github.com/linux-wpan/wpan-tools \
           "
SRCREV = "3f473f5136f89773997cb4fff2d8ed647734e2f5"

S = "${WORKDIR}/git"

inherit autotools pkgconfig
