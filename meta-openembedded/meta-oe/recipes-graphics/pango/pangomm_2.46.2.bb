SUMMARY = "C++ bindings for the pango library"
SECTION = "libs"
LICENSE = "LGPL-2.1-only & GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=d8045f3b8f929c1cb29a1e3fd737b499 \
                    file://COPYING.tools;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "mm-common cairomm glibmm pango"


inherit gnomebase features_check

ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"

SRC_URI[archive.sha256sum] = "57442ab4dc043877bfe3839915731ab2d693fc6634a71614422fb530c9eaa6f4"

FILES:${PN} = "${libdir}/lib*.so.*"
FILES:${PN}-dev += "${libdir}/*/include/ ${libdir}/pangomm-*/"

