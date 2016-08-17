SUMMARY = "C++ bindings for Cairo graphics library"

LICENSE = "LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=c46bda00ffbb0ba1dac22f8d087f54d9"

inherit autotools pkgconfig

DEPENDS = "cairo libsigc++-2.0"

SRC_URI = "http://cairographics.org/releases/cairomm-${PV}.tar.gz;name=archive"
SRC_URI[archive.md5sum] = "732a3ff5b57401eb5dfeef795a2a0c52"
SRC_URI[archive.sha256sum] = "ccf677098c1e08e189add0bd146f78498109f202575491a82f1815b6bc28008d"

FILES_${PN}-doc += "${datadir}/devhelp"
FILES_${PN}-dev += "${libdir}/cairomm-*/"

