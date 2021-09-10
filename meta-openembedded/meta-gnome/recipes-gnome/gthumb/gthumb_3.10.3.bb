SUMMARY = "Image viewer and browser"
LICENSE="GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"

GNOMEBASEBUILDCLASS = "meson"

REQUIRED_DISTRO_FEATURES = "polkit gobject-introspection-data"

DEPENDS = " \
    glib-2.0-native \
    bison-native \
    yelp-tools-native \
    gtk+3 \
    gsettings-desktop-schemas \
    zlib \
    jpeg \
    exiv2 \
    colord \
    lcms \
    libraw \
    librsvg \
    libsoup-2.4 \
    json-glib \
    libsecret \
"

inherit features_check gnomebase gnome-help gsettings itstool mime-xdg

SRC_URI[archive.sha256sum] = "dab73f77cc2963ebe90112972c301441d228af3003cfef3f8b7300a6d5d3c212"

FILES:${PN} += "${datadir}/metainfo"
