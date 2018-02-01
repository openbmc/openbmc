SUMMARY = "Tiny image-viewer"
HOMEPAGE = "http://goodies.xfce.org/projects/applications/ristretto"
SECTION = "x11/application"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=35d145429ad3cbf5308d1dc93f66376b"
DEPENDS = "exo libexif gtk+ dbus-glib libxfce4ui libxfce4util xfconf cairo file"

inherit xfce-app

RRECOMMENDS_${PN} += "tumbler"

SRC_URI[md5sum] = "a8d8bb6b8fa7f868cfa3ae778630946e"
SRC_URI[sha256sum] = "f8f3b77ca6fc77ddf8cff1bb52e5c5802c462663f72f324393b3a0360f6901b8"

FILES_${PN} += "${datadir}/appdata"
