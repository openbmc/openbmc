SUMMARY = "xdg-dbus-proxy is a filtering proxy for D-Bus connections"
HOMEPAGE = "https://github.com/flatpak/xdg-dbus-proxy"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = " \
    glib-2.0 \
    libxslt-native \
    docbook-xsl-stylesheets-native \
"

inherit meson pkgconfig ptest-gnome

SRC_URI = " \
    git://github.com/flatpak/xdg-dbus-proxy.git;protocol=https;branch=main \
    file://run-ptest \
    "

SRCREV = "1c1989e56f94b9eb3b7567f8a6e8a0aa16cba496"

PACKAGECONFIG = "${@bb.utils.contains('PTEST_ENABLED', '1', 'tests', '', d)}"
PACKAGECONFIG[tests] = "-Dtests=true -Dinstalled_tests=true,-Dtests=false -Dinstalled_tests=false"

RDEPENDS:${PN}-ptest += "dbus"

BBCLASSEXTEND = "native"
