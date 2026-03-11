SUMMARY = "Tiny image-viewer"
HOMEPAGE = "https://docs.xfce.org/apps/ristretto/start"
SECTION = "x11/application"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=35d145429ad3cbf5308d1dc93f66376b"

DEPENDS = "exo libexif libxfce4ui libxfce4util xfconf cairo file glib-2.0-native python3-packaging-native"

inherit xfce-app mime-xdg python3native

RRECOMMENDS:${PN} += "tumbler"

SRC_URI[sha256sum] = "5b9172ef704ae192a5338df6bee4e91a59edc65618c375bb4433ffb38e2126cb"

FILES:${PN} += "${datadir}/metainfo"
