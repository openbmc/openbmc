SUMMARY = "Netfilter logging library"
DESCRIPTION = "Userspace library providing a programming interface (API) to the Linux kernel netfilter log message (NFLOG)"
HOMEPAGE = "http://www.netfilter.org/projects/libnetfilter_log/index.html"
SECTION = "libs"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=8ca43cbc842c2336e835926c2166c28b"
DEPENDS = "libnfnetlink"

SRC_URI = "http://www.netfilter.org/projects/libnetfilter_log/files/libnetfilter_log-${PV}.tar.bz2;name=tar"
SRC_URI[tar.md5sum] = "2a4bb0654ae675a52d2e8d1c06090b94"
SRC_URI[tar.sha256sum] = "74e0fe75753dba3ac114531b5e73240452c789a3f3adccf5c51217da1d933b21"

S = "${WORKDIR}/libnetfilter_log-${PV}"

inherit autotools pkgconfig
