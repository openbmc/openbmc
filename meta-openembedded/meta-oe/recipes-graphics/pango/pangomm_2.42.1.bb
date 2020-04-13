SUMMARY = "C++ bindings for the pango library"
SECTION = "libs"
LICENSE = "LGPLv2.1 & GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=d8045f3b8f929c1cb29a1e3fd737b499 \
                    file://COPYING.tools;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "mm-common cairomm glibmm pango"

SHRT_VER = "${@d.getVar('PV').split('.')[0]}.${@d.getVar('PV').split('.')[1]}"

SRC_URI = "http://ftp.gnome.org/pub/GNOME/sources/pangomm/${SHRT_VER}/pangomm-${PV}.tar.xz"
SRC_URI[md5sum] = "339c48dd92ebd3a9911b231708f7a819"
SRC_URI[sha256sum] = "14bf04939930870d5cfa96860ed953ad2ce07c3fd8713add4a1bfe585589f40f"

inherit features_check autotools pkgconfig

REQUIRED_DISTRO_FEATURES = "x11"

EXTRA_OECONF = " --disable-documentation "

FILES_${PN} = "${libdir}/lib*.so.*"
FILES_${PN}-dev += "${libdir}/*/include/ ${libdir}/pangomm-*/"

