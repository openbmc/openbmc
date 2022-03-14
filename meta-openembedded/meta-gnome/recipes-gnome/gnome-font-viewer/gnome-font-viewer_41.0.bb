SUMMARY = "GNOME font viewer"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

SECTION = "x11/gnome"

DEPENDS = " \
    gtk+3 \
    gnome-desktop \
    libhandy \
"

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase gtk-icon-cache gettext features_check mime-xdg

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI += " file://0001-Fix-meson-build-with-meson-0.60.0.patch"
SRC_URI[archive.sha256sum] = "5dd410331be070e4e034397f2754980e073851d50a2119f2fbf96adc6fe2e876"

FILES:${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/metainfo \
    ${datadir}/thumbnailers \
"
