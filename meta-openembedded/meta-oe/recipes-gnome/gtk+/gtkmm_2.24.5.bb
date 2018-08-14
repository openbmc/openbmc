SUMMARY = "C++ bindings for the GTK+ toolkit"
HOMEPAGE = "http://www.gtkmm.org/"
SECTION = "libs"

LICENSE = "LGPLv2.1 & GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=d8045f3b8f929c1cb29a1e3fd737b499 \
                    file://COPYING.tools;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "atkmm pangomm glibmm gtk+ cairomm"

inherit gnomebase distro_features_check

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[archive.md5sum] = "6c59ae8bbff48fad9132f23af347acf1"
SRC_URI[archive.sha256sum] = "0680a53b7bf90b4e4bf444d1d89e6df41c777e0bacc96e9c09fc4dd2f5fe6b72"

EXTRA_OECONF = " --disable-documentation "

FILES_${PN}-dev += "${libdir}/*/include ${libdir}/*/proc/m4"
