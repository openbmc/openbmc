SUMMARY = "libportal provides GIO-style async APIs for most Flatpak portals."
DESCRIPTION = "It provides simple asynchronous wrappers for most Flatpak portals \
with a familiar GObject API along side the D-Bus API"
HOMEPAGE = "https://github.com/flatpak/libportal"
BUGTRACKER = "https://github.com/flatpak/libportal/issues"
LICENSE = "LGPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=3000208d539ec061b899bce1d9ce9404"

SRC_URI = "git://github.com/flatpak/${BPN}.git;protocol=https;branch=main"
SRCREV = "8f5dc8d192f6e31dafe69e35219e3b707bde71ce"

inherit meson gi-docgen gobject-introspection vala pkgconfig
GIDOCGEN_MESON_OPTION = 'docs'

DEPENDS += "glib-2.0 glib-2.0-native"

CAN_GTK = "${@bb.utils.contains_any('DISTRO_FEATURES', '${GTK3DISTROFEATURES}', '1', '0', d)}"

PACKAGECONFIG ??= "${@oe.utils.vartrue('CAN_GTK', 'gtk3', '', d)} \
                   ${@oe.utils.vartrue('CAN_GTK', '${@bb.utils.contains("DISTRO_FEATURES", "opengl", "gtk4", "", d)}', '', d)}"

PACKAGECONFIG[gtk3] = "-Dbackend-gtk3=enabled,-Dbackend-gtk3=disabled,gtk+3"
PACKAGECONFIG[gtk4] = "-Dbackend-gtk4=enabled,-Dbackend-gtk4=disabled,gtk4"

EXTRA_OEMESON = "${@bb.utils.contains('GI_DATA_ENABLED', 'True', '-Dvapi=true', '-Dvapi=false', d)} -Dbackend-qt5=disabled"

PACKAGES =+ "${PN}-gtk3 ${PN}-gtk4"

FILES:${PN}-gtk3 = "${libdir}/libportal-gtk3${SOLIBS} ${libdir}/girepository-1.0/*Gtk3-1.0.typelib"
FILES:${PN}-gtk4 = "${libdir}/libportal-gtk4${SOLIBS} ${libdir}/girepository-1.0/*Gtk4-1.0.typelib"
