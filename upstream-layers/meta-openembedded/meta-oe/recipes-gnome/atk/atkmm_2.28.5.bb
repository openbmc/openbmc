SUMMARY = "C++ bindings for the atk"
SECTION = "libs"

LICENSE = "GPL-2.0-only AND LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4bf661c1e3793e55c8d1051bc5e0ae21 \
                    file://COPYING.tools;md5=570a9b3749dd0463a1778803b12a6dce"

DEPENDS = "atk glibmm"


inherit gnomebase features_check

ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"

SRC_URI[archive.sha256sum] = "ae449192a582a2582a95e0602b15d792bbd639e836339b81ef916aa87540ac5c"

EXTRA_OEMESON = "-Dbuild-documentation=false"

FILES:${PN}-dev += "${libdir}/*/include ${libdir}/*/proc/m4"
