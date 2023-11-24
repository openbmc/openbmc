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

REQUIRED_DISTRO_FEATURES = "x11 opengl"

SRC_URI[archive.sha256sum] = "97cb6b68dda60de0ab3038383586f1e4bc1da5a48f44025bd6bbe74ea05c2b08"

FILES:${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/metainfo \
    ${datadir}/thumbnailers \
"
