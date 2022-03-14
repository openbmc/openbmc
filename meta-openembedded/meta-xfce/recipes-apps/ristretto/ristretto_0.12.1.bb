SUMMARY = "Tiny image-viewer"
HOMEPAGE = "https://docs.xfce.org/apps/ristretto/start"
SECTION = "x11/application"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=35d145429ad3cbf5308d1dc93f66376b"

DEPENDS = "exo libexif libxfce4ui libxfce4util xfconf cairo file"

inherit xfce-app mime-xdg

RRECOMMENDS:${PN} += "tumbler"

SRC_URI[sha256sum] = "13853f9ca18466a8e4788e92c7bde3388a93e8340283568f5dee1a9396ffd7ee"

FILES:${PN} += "${datadir}/metainfo"
