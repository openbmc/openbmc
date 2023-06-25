SUMMARY = "Tiny image-viewer"
HOMEPAGE = "https://docs.xfce.org/apps/ristretto/start"
SECTION = "x11/application"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=35d145429ad3cbf5308d1dc93f66376b"

DEPENDS = "exo libexif libxfce4ui libxfce4util xfconf cairo file"

inherit xfce-app mime-xdg

RRECOMMENDS:${PN} += "tumbler"

SRC_URI[sha256sum] = "d71affbf15245067124725b153c908a53208c4ca1ba2d4df1ec5a1308d53791e"

FILES:${PN} += "${datadir}/metainfo"
