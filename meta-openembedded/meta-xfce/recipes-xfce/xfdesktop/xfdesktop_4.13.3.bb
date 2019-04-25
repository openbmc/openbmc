SUMMARY = "Xfce4 Desktop Manager"
SECTION = "x11/base"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"
DEPENDS = "libxfce4util libxfce4ui libwnck xfconf dbus-glib dbus-glib-native thunar garcon exo"

inherit xfce distro_features_check

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[md5sum] = "f08944f1b69ce1c6e1fbb9d7f4cb1da7"
SRC_URI[sha256sum] = "80f7f28ef17638fe1cf4481db694731c208785a56b6c840005202f9c2db238f5"

PACKAGECONFIG ??= ""
PACKAGECONFIG[notify] = "--enable-notifications,--disable-notifications,libnotify"

FILES_${PN} += "${datadir}/backgrounds"
