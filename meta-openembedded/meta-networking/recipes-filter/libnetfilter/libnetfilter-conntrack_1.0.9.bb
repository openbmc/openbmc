SUMMARY = "Netfilter connection tracking library"
DESCRIPTION = "Userspace library providing a programming interface (API) to the Linux kernel netfilter connection tracking state table"
HOMEPAGE = "http://www.netfilter.org/projects/libnetfilter_conntrack/index.html"
SECTION = "libs"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=8ca43cbc842c2336e835926c2166c28b"
DEPENDS = "libnfnetlink libmnl"

SRC_URI = "https://www.netfilter.org/projects/libnetfilter_conntrack/files/libnetfilter_conntrack-${PV}.tar.bz2 \
           file://0001-conntrack-fix-build-with-kernel-5.15-and-musl.patch \
          "

SRC_URI[md5sum] = "596c722733cdf30f24d4418f34f999d9"
SRC_URI[sha256sum] = "67bd9df49fe34e8b82144f6dfb93b320f384a8ea59727e92ff8d18b5f4b579a8"

S = "${WORKDIR}/libnetfilter_conntrack-${PV}"

inherit autotools pkgconfig
