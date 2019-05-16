SUMMARY = "Netfilter connection tracking library"
DESCRIPTION = "Userspace library providing a programming interface (API) to the Linux kernel netfilter connection tracking state table"
HOMEPAGE = "http://www.netfilter.org/projects/libnetfilter_conntrack/index.html"
SECTION = "libs"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=8ca43cbc842c2336e835926c2166c28b"
DEPENDS = "libnfnetlink libmnl"

SRC_URI = "https://www.netfilter.org/projects/libnetfilter_conntrack/files/libnetfilter_conntrack-${PV}.tar.bz2"
SRC_URI[md5sum] = "013d182c2df716fcb5eb2a1fb7febd1f"
SRC_URI[sha256sum] = "33685351e29dff93cc21f5344b6e628e41e32b9f9e567f4bec0478eb41f989b6"

S = "${WORKDIR}/libnetfilter_conntrack-${PV}"

inherit autotools pkgconfig
