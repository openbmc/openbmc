SUMMARY = "Automatic management of removable drives and media for thunar"
SECTION = "x11"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
DEPENDS = "exo libnotify libgudev"

inherit xfce features_check perlnative

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[sha256sum] = "93b75c7ffbe246a21f4190295acc148e184be8df397e431b258d0d676e87fc65"

PACKAGECONFIG ??= "notify"
PACKAGECONFIG[notify] = "--enable-notifications,--disable-notifications,libnotify"

RDEPENDS:${PN} = "eject"
