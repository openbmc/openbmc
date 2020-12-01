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

SRC_URI += "file://8be361b6ce8f0f8053e1609decbdbdc164ec8448.patch"
SRC_URI[archive.sha256sum] = "7280880a082d631624d02a102dd547ceb59498da368311f3e49a06cff897f512"

FILES_${PN} += " \
    ${datadir}/gnome-shell \
    ${datadir}/metainfo \
    ${datadir}/dbus-1 \
"

