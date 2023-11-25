SUMMARY = "Image viewer and browser"
LICENSE="GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"


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
    libsoup \
    json-glib \
    libsecret \
"

GNOMEBASEBUILDCLASS = "autotools"
inherit features_check gnomebase gnome-help gsettings itstool mime-xdg

SRC_URI[archive.sha256sum] = "97f8afe522535216541ebbf1e3b546d12a6beb38a8f0eb85f26e676934aad425"

FILES:${PN} += "${datadir}/metainfo"
