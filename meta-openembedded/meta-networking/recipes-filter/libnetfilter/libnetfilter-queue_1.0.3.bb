SUMMARY = "Netfilter packet queue access library"
DESCRIPTION = "Userspace library providing a programming interface (API) to access the Linux kernel netfilter packet queue"
HOMEPAGE = "http://www.netfilter.org/projects/libnetfilter_queue/index.html"
SECTION = "libs"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=8ca43cbc842c2336e835926c2166c28b"
DEPENDS = "libnfnetlink libmnl"

SRCREV = "601abd1c71ccdf90753cf294c120ad43fb25dc54"

SRC_URI = "git://git.netfilter.org/libnetfilter_queue \
           file://0001-libnetfilter-queue-Declare-the-define-visivility-attribute-together.patch \
           "

S = "${WORKDIR}/git"

inherit autotools pkgconfig
