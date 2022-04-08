SUMMARY = "GNOME font viewer"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

SECTION = "x11/gnome"

DEPENDS = " \
    gtk4 \
    gnome-desktop \
    libadwaita \
"

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase gtk-icon-cache gettext features_check mime-xdg

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[archive.sha256sum] = "cfa2b8dfff21a105a1a021dadfa213f13627e6a179a77c7b59fdcedaca848dcc"

FILES:${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/metainfo \
    ${datadir}/thumbnailers \
"
