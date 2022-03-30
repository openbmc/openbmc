SUMMARY = "Netfilter logging library"
DESCRIPTION = "Userspace library providing a programming interface (API) to the Linux kernel netfilter log message (NFLOG)"
HOMEPAGE = "http://www.netfilter.org/projects/libnetfilter_log/index.html"
SECTION = "libs"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=8ca43cbc842c2336e835926c2166c28b"
DEPENDS = "libnfnetlink libmnl"
SRCREV = "b0e4be94c0b8f68d4e912402b93a130063c34e17"

SRC_URI = "git://git.netfilter.org/libnetfilter_log;branch=master"

S = "${WORKDIR}/git"

inherit autotools pkgconfig
