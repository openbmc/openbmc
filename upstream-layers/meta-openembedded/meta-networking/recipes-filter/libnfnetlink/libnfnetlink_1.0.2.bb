SUMMARY = "Low-level library for netfilter related kernel/userspace communication"
DESCRIPTION = "libnfnetlink is the low-level library for netfilter related \
kernel/userspace communication. It provides a generic messaging \
infrastructure for in-kernel netfilter subsystems (such as nfnetlink_log, \
nfnetlink_queue, nfnetlink_conntrack) and their respective users and/or \
management tools in userspace."
HOMEPAGE = "https://www.netfilter.org/projects/libnfnetlink/index.html"
SECTION = "libs"
LICENSE = "GPL-2.0-or-later"


LIC_FILES_CHKSUM = "file://COPYING;md5=8ca43cbc842c2336e835926c2166c28b"

SRC_URI = "https://www.netfilter.org/projects/libnfnetlink/files/${BPN}-${PV}.tar.bz2 \
          "

SRC_URI[md5sum] = "39d65185e2990562c64de05a08de8771"
SRC_URI[sha256sum] = "b064c7c3d426efb4786e60a8e6859b82ee2f2c5e49ffeea640cfe4fe33cbc376"

inherit autotools pkgconfig

BBCLASSEXTEND = "native"
