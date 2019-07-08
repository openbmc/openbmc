SUMMARY = "Netfilter connection tracking library"
DESCRIPTION = "Userspace library providing a programming interface (API) to the Linux kernel netfilter connection tracking state table"
HOMEPAGE = "http://www.netfilter.org/projects/libnetfilter_conntrack/index.html"
SECTION = "libs"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=8ca43cbc842c2336e835926c2166c28b"
DEPENDS = "libnfnetlink libmnl"

SRC_URI = "http://www.netfilter.org/projects/libnetfilter_conntrack/files/libnetfilter_conntrack-${PV}.tar.bz2;name=tar \
"
SRC_URI[tar.md5sum] = "7139c5f408dd9606ffecfd5dcda8175b"
SRC_URI[tar.sha256sum] = "efcc08021284e75f4d96d3581c5155a11f08fd63316b1938cbcb269c87f37feb"

S = "${WORKDIR}/libnetfilter_conntrack-${PV}"

inherit autotools pkgconfig
