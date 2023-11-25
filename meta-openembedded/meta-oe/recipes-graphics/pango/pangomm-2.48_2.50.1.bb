SUMMARY = "C++ bindings for the pango library"
SECTION = "libs"
LICENSE = "LGPL-2.1-only & GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=d8045f3b8f929c1cb29a1e3fd737b499 \
                    file://COPYING.tools;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "mm-common cairomm-1.16 glibmm-2.68 pango"

GNOMEBN = "pangomm"
inherit gnomebase features_check

SRC_URI[archive.sha256sum] = "ccc9923413e408c2bff637df663248327d72822f11e394b423e1c5652b7d9214"
REQUIRED_DISTRO_FEATURES = "x11"

S = "${WORKDIR}/${GNOMEBN}-${PV}"

FILES:${PN} = "${libdir}/lib*.so.*"
FILES:${PN}-dev += "${libdir}/*/include/ ${libdir}/pangomm-*/"

