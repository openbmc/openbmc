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

SRC_URI[archive.sha256sum] = "856333de86689f6a81c123f2db15d85db9addc438bc3574c36f15736aeae22e6"

EXTRA_OEMESON = "-Dbuild-demos=false"

FILES:${PN}-dev += "${libdir}/*/include ${libdir}/*/proc/m4"
