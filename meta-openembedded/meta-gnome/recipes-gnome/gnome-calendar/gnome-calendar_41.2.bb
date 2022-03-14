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

inherit gnomebase gsettings gtk-icon-cache gettext features_check upstream-version-is-even mime-xdg

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI += " file://0001-meson.build-fix-meson-0.61-builds.patch"
SRC_URI[archive.sha256sum] = "956b2f190322651c67fe667223896f8aa5acf33b70ada5a3b05a5361bda6611a"

FILES:${PN} += " \
    ${datadir}/gnome-shell \
    ${datadir}/metainfo \
    ${datadir}/dbus-1 \
"

