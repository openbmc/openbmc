SUMMARY = "GNOME calculator"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SECTION = "x11/gnome"

DEPENDS = " \
    yelp-tools-native \
    gtk+3 \
    libsoup-2.4 \
    libgee \
    libmpc \
    gtksourceview4 \
    libhandy \
"

GIR_MESON_OPTION = 'disable-introspection'
GIR_MESON_ENABLE_FLAG = 'false'
GIR_MESON_DISABLE_FLAG = 'true'

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase gobject-introspection gnome-help vala gtk-icon-cache gettext features_check

def gnome_verdir(v):
    return oe.utils.trim_version(v, 1)

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[archive.sha256sum] = "0267614afdb25d38b78411b42ebab7bc50c1b6340cc49bb68c0e432d7ddf8a34"

FILES:${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/metainfo \
    ${datadir}/gnome-shell \
"
