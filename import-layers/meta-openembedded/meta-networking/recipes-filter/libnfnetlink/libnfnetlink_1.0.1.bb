SUMMARY = "Low-level library for netfilter related kernel/userspace communication"
DESCRIPTION = "libnfnetlink is the low-level library for netfilter related \
kernel/userspace communication. It provides a generic messaging \
infrastructure for in-kernel netfilter subsystems (such as nfnetlink_log, \
nfnetlink_queue, nfnetlink_conntrack) and their respective users and/or \
management tools in userspace."
HOMEPAGE = "http://www.netfilter.org/projects/libnfnetlink/index.html"
SECTION = "libs"
LICENSE = "GPLv2+"


LIC_FILES_CHKSUM = "file://COPYING;md5=8ca43cbc842c2336e835926c2166c28b"

SRC_URI = "http://www.netfilter.org/projects/libnfnetlink/files/libnfnetlink-${PV}.tar.bz2;name=tar"
SRC_URI[tar.md5sum] = "98927583d2016a9fb1936fed992e2c5e"
SRC_URI[tar.sha256sum] = "f270e19de9127642d2a11589ef2ec97ef90a649a74f56cf9a96306b04817b51a"

inherit autotools pkgconfig
