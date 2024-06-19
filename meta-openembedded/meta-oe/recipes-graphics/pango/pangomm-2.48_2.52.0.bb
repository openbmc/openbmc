SUMMARY = "C++ bindings for the pango library"
SECTION = "libs"
LICENSE = "LGPL-2.1-only & GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=d8045f3b8f929c1cb29a1e3fd737b499 \
                    file://COPYING.tools;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "mm-common cairomm-1.16 glibmm-2.68 pango"

GNOMEBN = "pangomm"
inherit gnomebase features_check

SRC_URI[archive.sha256sum] = "34a134126a6484ff12f774358c36ecc44d0e9df094e1b83796d9774bb7d24947"
REQUIRED_DISTRO_FEATURES = "x11"

S = "${WORKDIR}/${GNOMEBN}-${PV}"

FILES:${PN} = "${libdir}/lib*.so.*"
FILES:${PN}-dev += "${libdir}/*/include/ ${libdir}/pangomm-*/"

