DESCRIPTION="Xfce4 Application Finder"
SECTION = "x11"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"
DEPENDS="glib-2.0 gtk+3 libxfce4util libxfce4ui garcon dbus-glib xfconf"

inherit xfce distro_features_check

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[md5sum] = "8b4c2ab413748fcd0cc51444418d5af3"
SRC_URI[sha256sum] = "fd774acbcab08dbb88bcbf28eecf73ec9f55b13e1f2058021b63f703c4989d97"

FILES_${PN} += "${datadir}/metainfo"
