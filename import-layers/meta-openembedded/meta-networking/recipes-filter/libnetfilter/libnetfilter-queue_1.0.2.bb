SUMMARY = "Netfilter packet queue access library"
DESCRIPTION = "Userspace library providing a programming interface (API) to access the Linux kernel netfilter packet queue"
HOMEPAGE = "http://www.netfilter.org/projects/libnetfilter_queue/index.html"
SECTION = "libs"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=8ca43cbc842c2336e835926c2166c28b"
DEPENDS = "libnfnetlink libmnl"

PV .= "+git${SRCREV}"
SRCREV = "981025e103d887fb6a9c9bb49c74ec323108d098"

SRC_URI = "git://git.netfilter.org/libnetfilter_queue \
           file://0001-Correct-typo-in-the-location-of-internal.h-in-includ.patch \
           file://0001-libnetfilter-queue-Declare-the-define-visivility-attribute-together.patch \
           "

S = "${WORKDIR}/git"

inherit autotools pkgconfig
