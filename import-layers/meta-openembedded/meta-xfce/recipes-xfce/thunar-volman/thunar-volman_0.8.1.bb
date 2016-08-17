SUMMARY = "Automatic management of removable drives and media for thunar"
SECTION = "x11"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"
DEPENDS = "exo glib-2.0 gtk+ libxfce4ui libxfce4util xfconf libnotify libgudev"

inherit xfce

SRC_URI[md5sum] = "65ab6e05b2e808d1dcc8d36683a59b7e"
SRC_URI[sha256sum] = "5a08bb5ce32c296a64dfbdb2406d4e45a208b2c91e4efa831dc28f1d6c2ac2bd"

PACKAGECONFIG ??= ""
PACKAGECONFIG[notify] = "--enable-notifications,--disable-notifications,libnotify"

RDEPENDS_${PN} = "eject"
