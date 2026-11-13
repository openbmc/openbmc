SUMMARY = "C++ bindings for the pango library"
SECTION = "libs"
LICENSE = "GPL-2.0-only AND LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4bf661c1e3793e55c8d1051bc5e0ae21 \
                    file://COPYING.tools;md5=570a9b3749dd0463a1778803b12a6dce"

DEPENDS = "mm-common cairomm-1.16 glibmm-2.68 pango"

GNOMEBN = "pangomm"
inherit gnomebase

SRC_URI[archive.sha256sum] = "217514c1a65035c2fce6e69e33b0d92bafa2594cc474e995a4473441b10f3a33"

S = "${UNPACKDIR}/${GNOMEBN}-${PV}"

FILES:${PN} = "${libdir}/lib*.so.*"
FILES:${PN}-dev += "${libdir}/*/include/ ${libdir}/pangomm-*/"

