SUMMARY = "C++ bindings for Cairo graphics library"

LICENSE = "LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=c46bda00ffbb0ba1dac22f8d087f54d9"

inherit gnomebase

DEPENDS = "cairo libsigc++-2.0"

SRC_URI[archive.md5sum] = "c62b476b61bd0abf7e9851f417d73291"
SRC_URI[archive.sha256sum] = "a54ada8394a86182525c0762e6f50db6b9212a2109280d13ec6a0b29bfd1afe6"

FILES_${PN}-doc += "${datadir}/devhelp"
FILES_${PN}-dev += "${libdir}/cairomm-*/"

