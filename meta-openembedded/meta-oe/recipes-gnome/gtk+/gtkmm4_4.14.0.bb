SUMMARY = "C++ bindings for the GTK+ toolkit V4"
HOMEPAGE = "http://www.gtkmm.org/"
SECTION = "libs"

LICENSE = "LGPL-2.1-only & GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c \
                    file://COPYING.tools;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "glib-2.0-native atkmm pangomm-2.48 glibmm gtk4 cairomm-1.16 gdk-pixbuf-native"

BPN = "gtkmm"

inherit gnomebase features_check

REQUIRED_DISTRO_FEATURES = "opengl x11"

SRC_URI[archive.sha256sum] = "9350a0444b744ca3dc69586ebd1b6707520922b6d9f4f232103ce603a271ecda"

EXTRA_OEMESON = "-Dbuild-demos=false"

FILES:${PN}-dev += "${libdir}/*/include ${libdir}/*/proc/m4"
