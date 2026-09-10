SUMMARY = "C++ bindings for the pango library"
SECTION = "libs"
LICENSE = "GPL-2.0-only AND LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4bf661c1e3793e55c8d1051bc5e0ae21 \
                    file://COPYING.tools;md5=570a9b3749dd0463a1778803b12a6dce"

DEPENDS = "mm-common cairomm glibmm pango"


inherit gnomebase features_check

ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"

SRC_URI[archive.sha256sum] = "38ca0b050b065de4e3da0c182df657437757063bbf0c4b6c9567ddba019b1d68"

FILES:${PN} = "${libdir}/lib*.so.*"
FILES:${PN}-dev += "${libdir}/*/include/ ${libdir}/pangomm-*/"

