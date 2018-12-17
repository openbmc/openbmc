SUMMARY = "Automatic management of removable drives and media for thunar"
SECTION = "x11"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"
DEPENDS = "exo glib-2.0 libxfce4ui libxfce4util xfconf libnotify libgudev"

inherit xfce distro_features_check

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[md5sum] = "3b403a4f846927391ea4bee765a055df"
SRC_URI[sha256sum] = "e4bffcfee15530e277fa80865eb1de31e63c7effaf8835c9fa7c71e5a0076b2e"

PACKAGECONFIG ??= ""
PACKAGECONFIG[notify] = "--enable-notifications,--disable-notifications,libnotify"

RDEPENDS_${PN} = "eject"
