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

SRC_URI[archive.md5sum] = "9524ed4e5c821d13eeda49ba2a78f024"
SRC_URI[archive.sha256sum] = "ddfe42ed2458a20a34de252854bcf4b52d3f0c671c045f56b42aa27c7542d2fd"
SRC_URI += "file://0001-do-not-build-demos.patch"

EXTRA_OECONF = " --disable-documentation "

FILES_${PN}-dev += "${libdir}/*/include ${libdir}/*/proc/m4"
