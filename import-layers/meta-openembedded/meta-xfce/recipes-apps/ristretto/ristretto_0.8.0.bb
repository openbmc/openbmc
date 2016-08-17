SUMMARY = "Tiny image-viewer"
HOMEPAGE = "http://goodies.xfce.org/projects/applications/ristretto"
SECTION = "x11/application"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=35d145429ad3cbf5308d1dc93f66376b"
DEPENDS = "exo libexif gtk+ dbus-glib libxfce4ui libxfce4util xfconf cairo"

inherit xfce-app

RRECOMMENDS_${PN} += "tumbler"

SRC_URI[md5sum] = "94c778850325a4e5a12e3433c8a05432"
SRC_URI[sha256sum] = "71625324cecda7199acbc95a3ea5132d0dcbf808771e7a209ea2b9503ae4f328"

FILES_${PN} += "${datadir}/appdata"
