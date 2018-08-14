SUMMARY = "Xfce4 Desktop Manager"
SECTION = "x11/base"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"
DEPENDS = "glib-2.0 gtk+ libxfce4util libxfce4ui libwnck xfconf dbus-glib dbus-glib-native thunar garcon exo"

inherit xfce distro_features_check

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[md5sum] = "7571889368be72df185ce2d470f37198"
SRC_URI[sha256sum] = "098a35510562907e1431d5adbfa8307484a235c1dec6a43e2d58d2ac4241f1cb"

PACKAGECONFIG ??= ""
PACKAGECONFIG[notify] = "--enable-notifications,--disable-notifications,libnotify"

FILES_${PN} += "${datadir}/backgrounds"
