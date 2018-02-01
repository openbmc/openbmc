SUMMARY = "Netfilter logging library"
DESCRIPTION = "Userspace library providing a programming interface (API) to the Linux kernel netfilter log message (NFLOG)"
HOMEPAGE = "http://www.netfilter.org/projects/libnetfilter_log/index.html"
SECTION = "libs"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=8ca43cbc842c2336e835926c2166c28b"
DEPENDS = "libnfnetlink libmnl"
SRCREV = "ba196a97e810746e5660fe3f57c87c0ed0f2b324"
PV .= "+git${SRCPV}"

SRC_URI = "git://git.netfilter.org/libnetfilter_log"

S = "${WORKDIR}/git"

inherit autotools pkgconfig
