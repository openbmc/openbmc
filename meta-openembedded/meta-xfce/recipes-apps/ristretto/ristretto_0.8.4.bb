SUMMARY = "Tiny image-viewer"
HOMEPAGE = "http://goodies.xfce.org/projects/applications/ristretto"
SECTION = "x11/application"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=35d145429ad3cbf5308d1dc93f66376b"
DEPENDS = "exo libexif gtk+ dbus-glib libxfce4ui libxfce4util xfconf cairo file"

inherit xfce-app

RRECOMMENDS_${PN} += "tumbler"

SRC_URI[md5sum] = "0142c8b9c492cf447e563b00c6204359"
SRC_URI[sha256sum] = "3a0be4526c93ec0ebbf2e63e69dd0c98f16b20b863d3b6426272592e5b00cea2"
SRC_URI += "file://0001-Fix-build-after-update-of-xfconf.patch"

FILES_${PN} += "${datadir}/appdata"
