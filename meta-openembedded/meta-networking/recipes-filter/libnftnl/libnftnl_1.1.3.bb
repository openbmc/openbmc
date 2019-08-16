SUMMARY = "Library for low-level interaction with nftables Netlink's API over libmnl"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=79808397c3355f163c012616125c9e26"
SECTION = "libs"
DEPENDS = "libmnl"

SRCREV = "d819a832e0214a3bec3679345f542644596a2850"
SRC_URI = "git://git.netfilter.org/libnftnl \
           file://0001-Move-exports-before-symbol-definition.patch \
           file://0002-avoid-naming-local-function-as-one-of-printf-family.patch \
           "

S = "${WORKDIR}/git"

inherit autotools pkgconfig
