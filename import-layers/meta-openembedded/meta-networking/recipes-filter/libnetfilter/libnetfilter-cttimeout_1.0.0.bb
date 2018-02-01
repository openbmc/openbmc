SUMMARY = "Netfilter connection tracking timeout library"
DESCRIPTION = "Userspace library providing a programming interface (API) to the Linux kernel netfilter fine-grain connection tracking timeout infrastructure"
SECTION = "libs"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=8ca43cbc842c2336e835926c2166c28b"
DEPENDS = "libmnl"

SRC_URI = "http://www.netfilter.org/projects/libnetfilter_cttimeout/files/libnetfilter_cttimeout-${PV}.tar.bz2;name=tar \
           file://libnetfilter-cttimeout-visibility-hidden.patch \
"
SRC_URI[tar.md5sum] = "7697437fc9ebb6f6b83df56a633db7f9"
SRC_URI[tar.sha256sum] = "aeab12754f557cba3ce2950a2029963d817490df7edb49880008b34d7ff8feba"

S = "${WORKDIR}/libnetfilter_cttimeout-${PV}"

inherit autotools pkgconfig
