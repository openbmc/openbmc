SUMMARY = "GNOME calendar"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=8f0e2cd40e05189ec81232da84bd6e1a"

SECTION = "x11/gnome"

DEPENDS = " \
    blueprint-compiler-native \
    fribidi \
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

export GI_TYPELIB_PATH = "${STAGING_LIBDIR}/girepository-1.0/"

SRC_URI[archive.sha256sum] = "4b75df071a52d98fb35e647d018030129d24d9007790d02457746c98617aeab0"

FILES:${PN} += " \
    ${datadir}/gnome-shell \
    ${datadir}/metainfo \
    ${datadir}/dbus-1 \
"
