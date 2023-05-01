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

SRC_URI[archive.sha256sum] = "a1511df30b228cc2ef1175dd9d2b93438ea912e25913404b263cf3d457bb9f97"

FILES:${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/metainfo \
    ${datadir}/thumbnailers \
"
