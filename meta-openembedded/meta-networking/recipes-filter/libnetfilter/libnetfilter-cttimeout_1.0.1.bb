SUMMARY = "Netfilter connection tracking timeout library"
DESCRIPTION = "Userspace library providing a programming interface (API) to the Linux kernel netfilter fine-grain connection tracking timeout infrastructure"
SECTION = "libs"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=8ca43cbc842c2336e835926c2166c28b"
DEPENDS = "libmnl"

SRC_URI = "https://www.netfilter.org/projects/libnetfilter_cttimeout/files/libnetfilter_cttimeout-${PV}.tar.bz2 \
          "

SRC_URI[md5sum] = "ac64b55952b79cb9910db95ce8883940"
SRC_URI[sha256sum] = "0b59da2f3204e1c80cb85d1f6d72285fc07b01a2f5678abf5dccfbbefd650325"

S = "${WORKDIR}/libnetfilter_cttimeout-${PV}"

inherit autotools pkgconfig
