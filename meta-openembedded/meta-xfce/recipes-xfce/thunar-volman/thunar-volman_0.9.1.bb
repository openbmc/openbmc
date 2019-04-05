SUMMARY = "Automatic management of removable drives and media for thunar"
SECTION = "x11"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"
DEPENDS = "exo glib-2.0 libxfce4ui libxfce4util xfconf libnotify libgudev"

inherit xfce distro_features_check

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[md5sum] = "4992a18f422918c33b562d6e66045754"
SRC_URI[sha256sum] = "cd87291a427e8a561c566dca3aa7e93b56fdce4caefc755dde0f59198f6db359"

PACKAGECONFIG ??= ""
PACKAGECONFIG[notify] = "--enable-notifications,--disable-notifications,libnotify"

RDEPENDS_${PN} = "eject"
