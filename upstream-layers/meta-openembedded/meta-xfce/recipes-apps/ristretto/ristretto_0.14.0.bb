SUMMARY = "Tiny image-viewer"
HOMEPAGE = "https://docs.xfce.org/apps/ristretto/start"
SECTION = "x11/application"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=35d145429ad3cbf5308d1dc93f66376b"

DEPENDS = "exo libexif libxfce4ui libxfce4util xfconf cairo file glib-2.0-native python3-packaging-native"

XFCE_COMPRESS_TYPE = "xz"
XFCEBASEBUILDCLASS = "meson"

inherit xfce-app mime-xdg python3native

RRECOMMENDS:${PN} += "tumbler"

SRC_URI[sha256sum] = "502cf1577de14b38132dc89e56884c5e10f86f6a028d8dde379a8839110fda55"

FILES:${PN} += "${datadir}/metainfo"
