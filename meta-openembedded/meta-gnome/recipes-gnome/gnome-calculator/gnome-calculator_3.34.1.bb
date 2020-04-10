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
"

GIR_MESON_OPTION = 'disable-introspection'
GIR_MESON_ENABLE_FLAG = 'false'
GIR_MESON_DISABLE_FLAG = 'true'

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase gobject-introspection gnome-help vala gtk-icon-cache gettext features_check upstream-version-is-even

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[archive.md5sum] = "9157b93a3f41fdad80df26c062b95c7b"
SRC_URI[archive.sha256sum] = "4d5348c2fbf01d040a2cb5e84de812c503911e1ea498a83e7eefff52c4417051"

FILES_${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/metainfo \
    ${datadir}/gnome-shell \
"
