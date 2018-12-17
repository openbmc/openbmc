SUMMARY = "Xfce4 Desktop Manager"
SECTION = "x11/base"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"
DEPENDS = "libxfce4util libxfce4ui libwnck xfconf dbus-glib dbus-glib-native thunar garcon exo"

inherit xfce distro_features_check

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[md5sum] = "c05569c1c39ccd314fb5da20d5086909"
SRC_URI[sha256sum] = "387f43d8a4615f44060c019cad212ac12df62c58eea2605813743116f71ad3bd"

PACKAGECONFIG ??= ""
PACKAGECONFIG[notify] = "--enable-notifications,--disable-notifications,libnotify"

FILES_${PN} += "${datadir}/backgrounds"
