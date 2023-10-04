SUMMARY = "GNOME calendar"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=8f0e2cd40e05189ec81232da84bd6e1a"

SECTION = "x11/gnome"

DEPENDS = " \
    gtk4 \
    libical \
    gsettings-desktop-schemas \
    evolution-data-server \
    libsoup \
    libdazzle \
    libadwaita \
    libgweather4 \
    geoclue \
    geocode-glib \
"

GNOMEBASEBUILDCLASS = "meson"
GTKIC_VERSION = '4'
inherit gnomebase gsettings gtk-icon-cache gettext features_check upstream-version-is-even mime-xdg

REQUIRED_DISTRO_FEATURES = "x11 opengl"

SRC_URI[archive.sha256sum] = "8c1483cbba4388db410875ed09d64e9003f929b555d704076a6fe7bd7c1e65b2"

do_install:prepend() {
    sed -i -e 's|${S}/src|/usr/src/debug/${PN}/${PV}-${PR}/src|g' ${B}/src/gcal-enum-types.h
    sed -i -e 's|${S}/src|/usr/src/debug/${PN}/${PV}-${PR}/src|g' ${B}/src/gcal-enum-types.c
}

FILES:${PN} += " \
    ${datadir}/gnome-shell \
    ${datadir}/metainfo \
    ${datadir}/dbus-1 \
"

