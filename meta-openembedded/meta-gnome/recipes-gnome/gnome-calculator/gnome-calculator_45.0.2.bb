SUMMARY = "GNOME calculator"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SECTION = "x11/gnome"

DEPENDS = " \
    yelp-tools-native \
    gtk4 \
    libsoup-3.0 \
    libgee \
    libxml2 \
    libmpc \
    gtksourceview5 \
    libadwaita \
"

GIR_MESON_OPTION = 'disable-introspection'
GIR_MESON_ENABLE_FLAG = 'false'
GIR_MESON_DISABLE_FLAG = 'true'
VALA_MESON_OPTION = ''

GTKIC_VERSION = '4'

inherit gnomebase gobject-introspection gnome-help vala gtk-icon-cache gettext features_check

def gnome_verdir(v):
    return oe.utils.trim_version(v, 1)

REQUIRED_DISTRO_FEATURES = "x11 opengl"

SRC_URI[archive.sha256sum] = "7dcbf32384897171cbe5483ec664d994e5e755e912ae1df911624f03c90867c2"

FILES:${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/metainfo \
    ${datadir}/gnome-shell \
"
