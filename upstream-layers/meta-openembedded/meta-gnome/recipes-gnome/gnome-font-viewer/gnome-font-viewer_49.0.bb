SUMMARY = "GNOME font viewer"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

SECTION = "x11/gnome"

DEPENDS = " \
    gtk4 \
    gnome-desktop \
    libadwaita \
"


inherit gnomebase gtk-icon-cache gettext features_check mime-xdg

REQUIRED_DISTRO_FEATURES = "x11 opengl"

SRC_URI[archive.sha256sum] = "7c018925c285771b55d7d1a6f15711c0c193d7450ed9871e20d44f2548562404"

FILES:${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/metainfo \
    ${datadir}/thumbnailers \
"
