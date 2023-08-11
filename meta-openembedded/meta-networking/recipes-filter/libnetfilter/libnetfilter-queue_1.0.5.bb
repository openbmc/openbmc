SUMMARY = "Netfilter packet queue access library"
DESCRIPTION = "Userspace library providing a programming interface (API) to access the Linux kernel netfilter packet queue"
HOMEPAGE = "http://www.netfilter.org/projects/libnetfilter_queue/index.html"
SECTION = "libs"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=8ca43cbc842c2336e835926c2166c28b"
DEPENDS = "libnfnetlink libmnl"

SRCREV = "2ff321690b8dafeca99ee8e9cafac71e36f292b9"

SRC_URI = "git://git.netfilter.org/libnetfilter_queue;branch=master \
           "

S = "${WORKDIR}/git"

inherit autotools pkgconfig

BBCLASSEXTEND = "native"
