SUMMARY = "Userspace tools for Linux IEEE 802.15.4 stack"
HOMEPAGE = "http://wpan.cakelab.org/releases/"
DESCRIPTION = "This is a set of utils to manage the Linux WPAN stack via \
netlink interface. This requires recent kernel with nl802154 interface."

LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://COPYING;md5=f008711099e40902ad091ed6396f33ea"

DEPENDS = "libnl"

SRC_URI = "git://github.com/linux-wpan/wpan-tools;branch=master;protocol=https"
SRCREV = "91b0f038aef2f62cb6a222d190b887fdfd6bc164"

S = "${WORKDIR}/git"

inherit autotools pkgconfig
