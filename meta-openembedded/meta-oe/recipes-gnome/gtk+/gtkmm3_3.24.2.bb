SUMMARY = "C++ bindings for the GTK+ toolkit V3"
HOMEPAGE = "http://www.gtkmm.org/"
SECTION = "libs"

LICENSE = "LGPLv2.1 & GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c \
                    file://COPYING.tools;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "atkmm pangomm glibmm gtk+3 cairomm"

BPN = "gtkmm"

inherit gnomebase features_check

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[archive.md5sum] = "e311db484ca9c53f1689d35f5f58a06b"
SRC_URI[archive.sha256sum] = "6d71091bcd1863133460d4188d04102810e9123de19706fb656b7bb915b4adc3"
SRC_URI += "file://0001-do-not-build-demos.patch"

EXTRA_OECONF = " --disable-documentation "

FILES_${PN}-dev += "${libdir}/*/include ${libdir}/*/proc/m4"
