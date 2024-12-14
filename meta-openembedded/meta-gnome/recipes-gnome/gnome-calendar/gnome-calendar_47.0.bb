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

GTKIC_VERSION = '4'
inherit gnomebase gsettings gtk-icon-cache gettext features_check upstream-version-is-even mime-xdg

REQUIRED_DISTRO_FEATURES = "x11 opengl"

SRC_URI[archive.sha256sum] = "3b03313f1c4d12dc821e07e209d1596d53eafa255d492d2ce5abf92ed1b51e76"

do_install:prepend() {
    sed -i -e 's|${S}/src|${TARGET_DBGSRC_DIR}/src|g' ${B}/src/gcal-enum-types.h
    sed -i -e 's|${S}/src|${TARGET_DBGSRC_DIR}/src|g' ${B}/src/gcal-enum-types.c
}

FILES:${PN} += " \
    ${datadir}/gnome-shell \
    ${datadir}/metainfo \
    ${datadir}/dbus-1 \
"

