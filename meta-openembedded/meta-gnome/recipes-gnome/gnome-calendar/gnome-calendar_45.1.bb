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

SRC_URI[archive.sha256sum] = "7fa8507543865aa7432bb5319830c87158b5447ca09cca45b607dc6796c71008"

do_install:prepend() {
    sed -i -e 's|${S}/src|/usr/src/debug/${PN}/${PV}-${PR}/src|g' ${B}/src/gcal-enum-types.h
    sed -i -e 's|${S}/src|/usr/src/debug/${PN}/${PV}-${PR}/src|g' ${B}/src/gcal-enum-types.c
}

FILES:${PN} += " \
    ${datadir}/gnome-shell \
    ${datadir}/metainfo \
    ${datadir}/dbus-1 \
"

