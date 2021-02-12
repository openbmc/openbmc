SUMMARY = "C++ bindings for the GTK+ toolkit V3"
HOMEPAGE = "http://www.gtkmm.org/"
SECTION = "libs"

LICENSE = "LGPLv2.1 & GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c \
                    file://COPYING.tools;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "glib-2.0-native atkmm pangomm glibmm gtk+3 cairomm"

BPN = "gtkmm"

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase features_check

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[archive.sha256sum] = "60497c4f7f354c3bd2557485f0254f8b7b4cf4bebc9fee0be26a77744eacd435"

EXTRA_OEMESON = "-Dbuild-demos=false"

FILES_${PN}-dev += "${libdir}/*/include ${libdir}/*/proc/m4"
