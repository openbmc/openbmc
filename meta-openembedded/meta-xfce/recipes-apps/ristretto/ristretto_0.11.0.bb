SUMMARY = "Tiny image-viewer"
HOMEPAGE = "https://docs.xfce.org/apps/ristretto/start"
SECTION = "x11/application"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=35d145429ad3cbf5308d1dc93f66376b"

DEPENDS = "exo libexif libxfce4ui libxfce4util xfconf cairo file"

inherit xfce-app mime-xdg

RRECOMMENDS:${PN} += "tumbler"

SRC_URI[sha256sum] = "877e30d412c8cbfa9706f4ac0cab1a478f5829beafb773addc7722ca0cb78823"

FILES:${PN} += "${datadir}/metainfo"
