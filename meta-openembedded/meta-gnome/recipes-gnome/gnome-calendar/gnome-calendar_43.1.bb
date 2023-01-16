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

inherit gnomebase gsettings gtk-icon-cache gettext features_check upstream-version-is-even mime-xdg

REQUIRED_DISTRO_FEATURES = "x11 opengl"

SRC_URI[archive.sha256sum] = "afa5fc66e2f691553cc7d517db2363cff2d62cd1b5df455e5f4f7133139d21f2"

FILES:${PN} += " \
    ${datadir}/gnome-shell \
    ${datadir}/metainfo \
    ${datadir}/dbus-1 \
"

