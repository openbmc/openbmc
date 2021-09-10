SUMMARY = "libportal provides GIO-style async APIs for most Flatpak portals."
DESCRIPTION = "It provides simple asynchronous wrappers for most Flatpak portals \
with a familiar GObject API along side the D-Bus API"
HOMEPAGE = "https://github.com/flatpak/libportal"
BUGTRACKER = "https://github.com/flatpak/libportal/issues"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=5f30f0716dfdd0d91eb439ebec522ec2"

SRC_URI = "git://github.com/flatpak/${BPN}.git;protocol=https"
SRCREV = "f68764e288ede516d902b131cc4fadded3804059"
S = "${WORKDIR}/git"

GTKDOC_MESON_OPTION = 'gtk_doc'

inherit meson gtk-doc

DEPENDS += "glib-2.0 glib-2.0-native"
