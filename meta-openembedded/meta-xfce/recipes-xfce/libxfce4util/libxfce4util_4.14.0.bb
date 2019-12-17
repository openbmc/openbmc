SUMMARY = "Basic utility library for Xfce4"
SECTION = "x11/libs"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=252890d9eee26aab7b432e8b8a616475"
DEPENDS = "intltool-native xfce4-dev-tools-native glib-2.0"

inherit xfce gtk-doc gobject-introspection

SRC_URI[md5sum] = "46f44e36acc3abf1a5ba814c22a773cb"
SRC_URI[sha256sum] = "32ad79b7992ec3fd863e8ff2f03eebda8740363ef9d7d910a35963ac1c1a6324"
