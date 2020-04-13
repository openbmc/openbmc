SUMMARY = "Netfilter connection tracking library"
DESCRIPTION = "Userspace library providing a programming interface (API) to the Linux kernel netfilter connection tracking state table"
HOMEPAGE = "http://www.netfilter.org/projects/libnetfilter_conntrack/index.html"
SECTION = "libs"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=8ca43cbc842c2336e835926c2166c28b"
DEPENDS = "libnfnetlink libmnl"

SRC_URI = "https://www.netfilter.org/projects/libnetfilter_conntrack/files/libnetfilter_conntrack-${PV}.tar.bz2"
SRC_URI[md5sum] = "3121b55acf97322db830da75d8407cba"
SRC_URI[sha256sum] = "0cd13be008923528687af6c6b860f35392d49251c04ee0648282d36b1faec1cf"

S = "${WORKDIR}/libnetfilter_conntrack-${PV}"

inherit autotools pkgconfig
