SUMMARY = "Netfilter packet queue access library"
DESCRIPTION = "Userspace library providing a programming interface (API) to access the Linux kernel netfilter packet queue"
HOMEPAGE = "http://www.netfilter.org/projects/libnetfilter_queue/index.html"
SECTION = "libs"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=8ca43cbc842c2336e835926c2166c28b"
DEPENDS = "libnfnetlink libmnl"

SRC_URI = "http://www.netfilter.org/projects/libnetfilter_queue/files/libnetfilter_queue-${PV}.tar.bz2;name=tar"
SRC_URI[tar.md5sum] = "df09befac35cb215865b39a36c96a3fa"
SRC_URI[tar.sha256sum] = "838490eb5dbe358f9669823704982f5313a8d397111562373200203f93ac1a32"

S = "${WORKDIR}/libnetfilter_queue-${PV}"

inherit autotools pkgconfig
