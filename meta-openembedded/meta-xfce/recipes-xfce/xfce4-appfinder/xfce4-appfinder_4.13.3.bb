DESCRIPTION="Xfce4 Application Finder"
SECTION = "x11"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"
DEPENDS="glib-2.0 gtk+3 libxfce4util libxfce4ui garcon dbus-glib xfconf"

inherit xfce distro_features_check

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[md5sum] = "c2069a14c85c8a3e537b2d4c552d36d2"
SRC_URI[sha256sum] = "d738082a5fb01e42ea6333803012fd80258061f444afbbbd7b05a0f620a32ba6"

FILES_${PN} += "${datadir}/metainfo"
