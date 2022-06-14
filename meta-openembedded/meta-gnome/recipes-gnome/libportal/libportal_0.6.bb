SUMMARY = "libportal provides GIO-style async APIs for most Flatpak portals."
DESCRIPTION = "It provides simple asynchronous wrappers for most Flatpak portals \
with a familiar GObject API along side the D-Bus API"
HOMEPAGE = "https://github.com/flatpak/libportal"
BUGTRACKER = "https://github.com/flatpak/libportal/issues"
LICENSE = "LGPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=3000208d539ec061b899bce1d9ce9404"

SRC_URI = "git://github.com/flatpak/${BPN}.git;protocol=https;branch=main"
SRCREV = "13df0b887a7eb7b0f9b14069561a41f62e813155"
S = "${WORKDIR}/git"

inherit meson gtk-doc gobject-introspection features_check

ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"

DEPENDS += "glib-2.0 glib-2.0-native gtk+3"

EXTRA_OEMESON = "-Dbackends=gtk3 -Dvapi=false"
