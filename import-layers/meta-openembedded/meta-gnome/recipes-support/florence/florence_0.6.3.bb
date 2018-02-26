SUMMARY = "Florence is a virtual keyboard for touch screens"
HOMEPAGE = "http://florence.sourceforge.net/english.html"

#NOTICE: If florence can't find its gconf settings, you need to start florence with --config for one time and save the configuration once.

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"

DEPENDS = "gtk+3 libxml2 libglade librsvg libxtst gconf gconf-native cairo intltool-native gnome-doc-utils libnotify gstreamer1.0"

SRC_URI = "${SOURCEFORGE_MIRROR}/project/${PN}/${PN}/${PV}/${PN}-${PV}.tar.bz2"
SRC_URI[md5sum] = "8775051d7352f75dec5a86dc9964e8e0"
SRC_URI[sha256sum] = "422992fd07d285be73cce721a203e22cee21320d69b0fda1579ce62944c5091e"

inherit gettext autotools gconf pkgconfig

EXTRA_OECONF = "--disable-scrollkeeper --without-docs --without-at-spi --without-panelapplet --without-xrecord"

FILES_${PN} += "${datadir}/glib-2.0/schemas"
