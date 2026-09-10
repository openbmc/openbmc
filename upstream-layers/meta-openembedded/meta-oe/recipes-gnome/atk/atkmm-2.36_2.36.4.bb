SUMMARY = "C++ bindings for the atk"
SECTION = "libs"

LICENSE = "GPL-2.0-only AND LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4bf661c1e3793e55c8d1051bc5e0ae21 \
                    file://COPYING.tools;md5=570a9b3749dd0463a1778803b12a6dce"

DEPENDS = "atk glibmm-2.68"

GNOMEBN = "atkmm"

inherit gnomebase features_check

ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"

SRC_URI[archive.sha256sum] = "19cd0758ed752cb89f5bf02247663dfad0926d9351984a20e3c6cf7da62552ac"

S = "${UNPACKDIR}/${GNOMEBN}-${PV}"

EXTRA_OEMESON = "-Dbuild-documentation=false"

FILES:${PN}-dev += "${libdir}/*/include ${libdir}/*/proc/m4"
