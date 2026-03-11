SUMMARY = "C++ bindings for the GTK+ toolkit V3"
HOMEPAGE = "http://www.gtkmm.org/"
SECTION = "libs"

LICENSE = "LGPL-2.1-only & GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c \
                    file://COPYING.tools;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "glib-2.0-native atkmm pangomm glibmm gtk+3 cairomm gdk-pixbuf-native"

BPN = "gtkmm"


inherit gnomebase features_check

ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"

SRC_URI[archive.sha256sum] = "1d7a35af9c5ceccacb244ee3c2deb9b245720d8510ac5c7e6f4b6f9947e6789c"

EXTRA_OEMESON = "-Dbuild-demos=false"

FILES:${PN}-dev += "${libdir}/*/include ${libdir}/*/proc/m4"
