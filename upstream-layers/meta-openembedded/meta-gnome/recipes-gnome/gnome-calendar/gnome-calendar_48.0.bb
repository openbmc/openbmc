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
    libadwaita \
    libgweather4 \
    geoclue \
"

GTKIC_VERSION = '4'
inherit gnomebase gsettings gtk-icon-cache gettext features_check upstream-version-is-even mime-xdg

REQUIRED_DISTRO_FEATURES = "opengl"
ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"

SRC_URI += "file://0001-Support-building-against-libical-4.0.patch"

SRC_URI[archive.sha256sum] = "e69a5a66be820105a4d823d4dba9b4e6ef9f6e7523978aaf33361ebbb993e604"

FILES:${PN} += " \
    ${datadir}/gnome-shell \
    ${datadir}/metainfo \
    ${datadir}/dbus-1 \
"
