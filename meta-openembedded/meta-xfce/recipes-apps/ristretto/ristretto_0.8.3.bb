SUMMARY = "Tiny image-viewer"
HOMEPAGE = "http://goodies.xfce.org/projects/applications/ristretto"
SECTION = "x11/application"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=35d145429ad3cbf5308d1dc93f66376b"
DEPENDS = "exo libexif gtk+ dbus-glib libxfce4ui libxfce4util xfconf cairo file"

inherit xfce-app

RRECOMMENDS_${PN} += "tumbler"

SRC_URI[md5sum] = "5866b4e11c32a38f72bc737239102544"
SRC_URI[sha256sum] = "8c9c11760816dfd9ed57fb8b9df86c6a98a2604ab551be3133996a1c32ca2665"
SRC_URI += "file://0001-Fix-build-after-update-of-xfconf.patch"

FILES_${PN} += "${datadir}/appdata"
