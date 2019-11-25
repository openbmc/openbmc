SUMMARY = "Automatic management of removable drives and media for thunar"
SECTION = "x11"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"
DEPENDS = "exo libnotify libgudev"

inherit xfce features_check

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[md5sum] = "f9ef39ed4bbff05eb74058dfff24dd84"
SRC_URI[sha256sum] = "7ea7c6693334f2248cf399586af8974dfb7db9aad685ee31ac100e62e19a1837"

PACKAGECONFIG ??= ""
PACKAGECONFIG[notify] = "--enable-notifications,--disable-notifications,libnotify"

RDEPENDS_${PN} = "eject"
