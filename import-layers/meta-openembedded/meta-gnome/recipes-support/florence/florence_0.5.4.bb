DESCRIPTION = "Florence is a virtual keyboard (also called on-screen-keyboard), which allows the user to input text through a touchscreen."
HOMEPAGE = "http://florence.sourceforge.net/english.html"

#NOTICE: If florence can't find its gconf settings, you need to start florence with --config for one time and save the configuration once.

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"

PR = "r1"

DEPENDS = "gtk+ libxml2 libglade librsvg libxtst gconf gconf-native cairo intltool-native gnome-doc-utils libnotify gstreamer"

SRC_URI = "${SOURCEFORGE_MIRROR}/florence/florence/${PV}-gtk2/florence-${PV}.tar.bz2 \
           file://fix-no-atspi-compile.patch"
SRC_URI[md5sum] = "8d5c2367a28485bfcf577650b0badab7"
SRC_URI[sha256sum] = "26d33aa20d5fbf34ceeded4c41cb922d2988b6082e33d9acc46dd7bfe56d31a1"

inherit gettext autotools gconf pkgconfig

EXTRA_OECONF = "--disable-scrollkeeper --without-docs --without-at-spi --without-panelapplet --without-xrecord"
