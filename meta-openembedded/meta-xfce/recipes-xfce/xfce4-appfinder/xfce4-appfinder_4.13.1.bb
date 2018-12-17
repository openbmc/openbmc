DESCRIPTION="Xfce4 Application Finder"
SECTION = "x11"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"
DEPENDS="glib-2.0 gtk+3 libxfce4util libxfce4ui garcon dbus-glib xfconf"

inherit xfce distro_features_check

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[md5sum] = "9e2a1f9b4ae3183c461b79891224c33f"
SRC_URI[sha256sum] = "5cabb27bbe0a0864b785e1a38535a91516763e38d6bff41a3915a61d28254b03"

FILES_${PN} += "${datadir}/appdata"
