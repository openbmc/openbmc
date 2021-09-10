SUMMARY = "GNOME font viewer"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

SECTION = "x11/gnome"

DEPENDS = " \
    gtk+3 \
    gnome-desktop3 \
    libhandy \
"

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase gtk-icon-cache gettext features_check mime-xdg

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[archive.sha256sum] = "d2cc7686946690dc274a5d0c72841d358d0ccd42d3f34993c698bdf13588fe42"

FILES:${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/metainfo \
    ${datadir}/thumbnailers \
"
