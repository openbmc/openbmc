SUMMARY = "WBXML parsing and encoding library"
HOMEPAGE = "http://libwbxml.opensync.org/"
SECTION = "libs"
LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=c1128ee5341ccd5927d8bafe4b6266e1"

DEPENDS = "expat"

SRC_URI = "${SOURCEFORGE_MIRROR}/libwbxml/libwbxml-${PV}.tar.gz"

SRC_URI[md5sum] = "f5031e9f730ffd9dc6a2d1ded585e1d1"
SRC_URI[sha256sum] = "a057daa098f12838eb4e635bb28413027f1b73819872c3fbf64e3207790a3f7d"

S = "${WORKDIR}/libwbxml-${PV}"

inherit cmake pkgconfig

EXTRA_OECMAKE = "-DLIB_SUFFIX=${@d.getVar('baselib', True).replace('lib', '')}"

PACKAGES += "${PN}-tools"

FILES_${PN}-tools = "${bindir}"
FILES_${PN} = "${libdir}/*.so.*"
