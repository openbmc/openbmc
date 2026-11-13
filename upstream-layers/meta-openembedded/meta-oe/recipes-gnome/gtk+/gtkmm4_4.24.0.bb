SUMMARY = "C++ bindings for the GTK+ toolkit V4"
HOMEPAGE = "http://www.gtkmm.org/"
SECTION = "libs"

LICENSE = "GPL-2.0-only AND LGPL-2.1-only"
LIC_FILES_CHKSUM = " \
	file://COPYING;md5=4bf661c1e3793e55c8d1051bc5e0ae21 \
	file://COPYING.tools;md5=570a9b3749dd0463a1778803b12a6dce \
"

DEPENDS = "glib-2.0-native pangomm-2.48 glibmm gtk4 cairomm-1.16 gdk-pixbuf-native"

GNOMEBN = "gtkmm"

inherit gnomebase features_check

REQUIRED_DISTRO_FEATURES = "opengl"

SRC_URI[archive.sha256sum] = "7fd9cea356e7d3b74bf7d2a51d7e0e6763f3f9f1cddc5e77c8b0b5b7fa9d5e5a"

EXTRA_OEMESON = "-Dbuild-demos=false"

FILES:${PN}-dev += "${libdir}/*/include ${libdir}/*/proc/m4"
