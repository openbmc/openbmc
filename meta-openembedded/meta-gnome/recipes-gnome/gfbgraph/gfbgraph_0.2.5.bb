SUMMARY = "A GObject library for Facebook Graph API"
SECTION = "x11/gnome"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=5804fe91d3294da4ac47c02b454bbc8a"

DEPENDS = " \
    glib-2.0 \
    json-glib \
    librest \
    libsoup-2.4 \
    gnome-online-accounts \
"
GNOMEBASEBUILDCLASS = "autotools"
inherit gnomebase gtk-doc gobject-introspection pkgconfig features_check

# for gnome-online-accounts
REQUIRED_DISTRO_FEATURES = "x11"

#SRC_URI += " file://0001-Update-rest-requirement-to-rest-1.0.patch"
SRC_URI[archive.sha256sum] = "9cb381b3f78ba1136df97af3f06e3b11dcc2ab339ac08f74eda0f8057d6603e3"

do_install:append() {
    # they install all the autotools files (NEWS AUTHORS..) to /usr/doc which
    # is not a standard path exactly
    rm -rf ${D}${prefix}/doc
}
