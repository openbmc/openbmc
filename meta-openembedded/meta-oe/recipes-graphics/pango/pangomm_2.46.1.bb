SUMMARY = "C++ bindings for the pango library"
SECTION = "libs"
LICENSE = "LGPLv2.1 & GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=d8045f3b8f929c1cb29a1e3fd737b499 \
                    file://COPYING.tools;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "mm-common cairomm glibmm pango"

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase features_check

SRC_URI[archive.sha256sum] = "c885013fe61a4c5117fda395770d507563411c63e49f4a3aced4c9efe34d9975"

REQUIRED_DISTRO_FEATURES = "x11"

FILES:${PN} = "${libdir}/lib*.so.*"
FILES:${PN}-dev += "${libdir}/*/include/ ${libdir}/pangomm-*/"

