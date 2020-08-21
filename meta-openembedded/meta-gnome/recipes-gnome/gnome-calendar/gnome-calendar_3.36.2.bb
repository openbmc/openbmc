SUMMARY = "GNOME calendar"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=8f0e2cd40e05189ec81232da84bd6e1a"

SECTION = "x11/gnome"

DEPENDS = " \
    gtk+3 \
    libical \
    gsettings-desktop-schemas \
    evolution-data-server \
    libsoup-2.4 \
    libdazzle \
    libhandy \
    libgweather \
    geoclue \
"

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase gsettings gtk-icon-cache gettext features_check upstream-version-is-even

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[archive.sha256sum] = "d0b05345c0555a085e6e5426eab49494aba2826c856eb06fd7fdb762ec0c4c1f"

FILES_${PN} += " \
    ${datadir}/gnome-shell \
    ${datadir}/metainfo \
    ${datadir}/dbus-1 \
"

