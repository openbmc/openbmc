SUMMARY = "Xfce4 Desktop Manager"
SECTION = "x11/base"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"
DEPENDS = "libxfce4ui libwnck thunar garcon exo"

inherit xfce distro_features_check

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[md5sum] = "de4b8f6687862ad46dbe4e1ced453f4d"
SRC_URI[sha256sum] = "f705a016246ec54ab0c688d1a0577f6c2b45a446690aa8d9e5a7ac23efebf882"

PACKAGECONFIG ??= ""
PACKAGECONFIG[notify] = "--enable-notifications,--disable-notifications,libnotify"

FILES_${PN} += "${datadir}/backgrounds"
