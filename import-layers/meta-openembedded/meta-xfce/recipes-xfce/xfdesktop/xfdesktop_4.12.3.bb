SUMMARY = "Xfce4 Desktop Manager"
SECTION = "x11/base"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"
DEPENDS = "glib-2.0 gtk+ libxfce4util libxfce4ui libwnck xfconf dbus-glib thunar garcon exo"

inherit xfce

SRC_URI[md5sum] = "cb34f4f333d7d122f1688d2f155202c8"
SRC_URI[sha256sum] = "a8a8d93744d842ca6ac1f9bd2c8789ee178937bca7e170e5239cbdbef30520ac"

PACKAGECONFIG ??= ""
PACKAGECONFIG[notify] = "--enable-notifications,--disable-notifications,libnotify"

FILES_${PN} += "${datadir}/backgrounds"
