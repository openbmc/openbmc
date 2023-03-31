SUMMARY = "Netfilter connection tracking helper library"
DESCRIPTION = "Userspace library providing a programming interface (API) to the Linux kernel netfilter user-space helper infrastructure"
HOMEPAGE = "https://www.netfilter.org/projects/libnetfilter_cthelper/index.html"
SECTION = "libs"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=8ca43cbc842c2336e835926c2166c28b"
DEPENDS = "libmnl"

SRC_URI = "https://www.netfilter.org/projects/libnetfilter_cthelper/files/libnetfilter_cthelper-${PV}.tar.bz2 \
          "

SRC_URI[md5sum] = "e59279645fe65d40dd7dfc82a797ca5b"
SRC_URI[sha256sum] = "14073d5487233897355d3ff04ddc1c8d03cc5ba8d2356236aa88161a9f2dc912"

S = "${WORKDIR}/libnetfilter_cthelper-${PV}"

inherit autotools pkgconfig
