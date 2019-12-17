SUMMARY = "Objects and helper methods to help reading and writing AppStream metadata"
HOMEPAGE = "https://people.freedesktop.org/~hughsient/appstream-glib/index.htm"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = " \
    gperf-native \
    glib-2.0 \
    gtk+3 \
    libyaml \
    libarchive \
    libsoup-2.4 \
    json-glib \
    gdk-pixbuf \
    freetype \
    fontconfig \
    libstemmer \
"

inherit meson gobject-introspection gettext bash-completion

SRC_URI = "https://people.freedesktop.org/~hughsient/${BPN}/releases/${BP}.tar.xz"
SRC_URI[md5sum] = "78306049412396a72746186452abdf66"
SRC_URI[sha256sum] = "04f290d73bc865071112076b8a3345df2730783a16af976fe3becfd2f50d5992"

EXTRA_OEMESON = " \
    -Drpm=false \
"

FILES_${PN} += "${libdir}/asb-plugins-5"

FILES_${PN}-dev += " \
    ${datadir}/installed-tests \
    ${datadir}/gettext \
"

BBCLASSEXTEND = "native"
