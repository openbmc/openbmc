SUMMARY = "Tiny image-viewer"
HOMEPAGE = "https://docs.xfce.org/apps/ristretto/start"
SECTION = "x11/application"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=35d145429ad3cbf5308d1dc93f66376b"

DEPENDS = "exo libexif libxfce4ui libxfce4util xfconf cairo file"

inherit xfce-app mime-xdg

RRECOMMENDS_${PN} += "tumbler"

SRC_URI[md5sum] = "4249e14fba78728481d89ce61a8771fc"
SRC_URI[sha256sum] = "16225dd74245eb6e0f82b9c72c6112f161bb8d8b11f3fc77277b7bc3432d4769"

FILES_${PN} += "${datadir}/metainfo"
