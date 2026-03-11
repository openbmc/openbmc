SUMMARY = "WBXML parsing and encoding library"
HOMEPAGE = "https://github.com/libwbxml/libwbxml"
SECTION = "libs"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=b17146e5186e05865f75664e910ace79"

DEPENDS = "expat"

SRC_URI = "git://github.com/libwbxml/libwbxml;branch=master;protocol=https;tag=libwbxml-${PV}"
SRCREV = "e58b1f19f11dbadff53e5b486b8c4b16639a656a"

inherit cmake pkgconfig

EXTRA_OECMAKE = "-DLIB_SUFFIX=${@d.getVar('baselib').replace('lib', '')}"

PACKAGES += "${PN}-tools"

FILES:${PN}-tools = "${bindir}"
FILES:${PN} = "${libdir}/*.so.*"
