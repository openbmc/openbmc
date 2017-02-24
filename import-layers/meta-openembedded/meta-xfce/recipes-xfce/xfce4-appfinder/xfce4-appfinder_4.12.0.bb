DESCRIPTION="Xfce4 Application Finder"
SECTION = "x11"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"
DEPENDS="glib-2.0 gtk+ libxfce4util libxfce4ui garcon dbus-glib xfconf"

inherit xfce distro_features_check

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[md5sum] = "0b238b30686388c507c119b12664f1a1"
SRC_URI[sha256sum] = "2ad4a58019a76a6b64a816050db25f96854917c2f2e89d6a9df6c18e6c84c567"

FILES_${PN} += "${datadir}/appdata"
