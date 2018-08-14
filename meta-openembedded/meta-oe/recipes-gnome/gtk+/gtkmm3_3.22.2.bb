SUMMARY = "C++ bindings for the GTK+ toolkit V3"
HOMEPAGE = "http://www.gtkmm.org/"
SECTION = "libs"

LICENSE = "LGPLv2.1 & GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c \
                    file://COPYING.tools;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "atkmm pangomm glibmm gtk+3 cairomm"

BPN = "gtkmm"

inherit gnomebase distro_features_check

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[archive.md5sum] = "883e9046768b3a5afc8e365988823e77"
SRC_URI[archive.sha256sum] = "91afd98a31519536f5f397c2d79696e3d53143b80b75778521ca7b48cb280090"
SRC_URI += "file://0001-do-not-build-demos.patch"

EXTRA_OECONF = " --disable-documentation "

FILES_${PN}-dev += "${libdir}/*/include ${libdir}/*/proc/m4"
