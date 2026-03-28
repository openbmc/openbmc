SUMMARY = "C++ bindings for the GTK+ toolkit V4"
HOMEPAGE = "http://www.gtkmm.org/"
SECTION = "libs"

LICENSE = "LGPL-2.1-only & GPL-2.0-only"
LIC_FILES_CHKSUM = " \
	file://COPYING;md5=4bf661c1e3793e55c8d1051bc5e0ae21 \
	file://COPYING.tools;md5=570a9b3749dd0463a1778803b12a6dce \
"

DEPENDS = "glib-2.0-native atkmm pangomm-2.48 glibmm gtk4 cairomm-1.16 gdk-pixbuf-native"

GNOMEBN = "gtkmm"

inherit gnomebase features_check

REQUIRED_DISTRO_FEATURES = "opengl x11"

SRC_URI[archive.sha256sum] = "daad9bf9b70f90975f91781fc7a656c923a91374261f576c883cd3aebd59c833"

EXTRA_OEMESON = "-Dbuild-demos=false"

FILES:${PN}-dev += "${libdir}/*/include ${libdir}/*/proc/m4"
