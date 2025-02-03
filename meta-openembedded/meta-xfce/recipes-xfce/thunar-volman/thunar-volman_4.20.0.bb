SUMMARY = "Automatic management of removable drives and media for thunar"
SECTION = "x11"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
DEPENDS = "exo libnotify libgudev"

inherit xfce features_check perlnative

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[sha256sum] = "b0dad852959b515b8fbfd1ed552e362340347d26d5246e7f1b973027131eb1da"

PACKAGECONFIG ??= "notify"
PACKAGECONFIG[notify] = "--enable-notifications,--disable-notifications,libnotify"

RDEPENDS:${PN} = "eject"
