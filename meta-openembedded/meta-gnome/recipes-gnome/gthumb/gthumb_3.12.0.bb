SUMMARY = "Image viewer and browser"
LICENSE="GPL-2.0-only"
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

SRC_URI += " file://0001-LINGUAS-removed-duplicated-entry.patch"
SRC_URI[archive.sha256sum] = "3deffc030384e64b57361c437c79b481ae1489ef44c87ae856e81bb10d8e383f"

FILES:${PN} += "${datadir}/metainfo"
