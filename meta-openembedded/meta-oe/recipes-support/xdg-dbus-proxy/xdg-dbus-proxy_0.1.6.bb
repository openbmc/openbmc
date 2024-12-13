SUMMARY = "xdg-dbus-proxy is a filtering proxy for D-Bus connections"
HOMEPAGE = "https://github.com/flatpak/xdg-dbus-proxy"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = " \
    glib-2.0 \
    libxslt-native \
    docbook-xsl-stylesheets-native \
"

inherit meson pkgconfig

SRC_URI = "git://github.com/flatpak/xdg-dbus-proxy.git;protocol=https;branch=main"

S = "${WORKDIR}/git"
SRCREV = "1c1989e56f94b9eb3b7567f8a6e8a0aa16cba496"

BBCLASSEXTEND = "native"
