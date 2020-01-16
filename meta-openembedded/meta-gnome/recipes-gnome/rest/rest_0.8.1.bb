SUMMARY = "library to access web services that claim to be "RESTful""
HOMPAGE = "https://wiki.gnome.org/Projects/Librest"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=2d5025d4aa3495befef8f17206a5b0a1"

GNOMEBASEBUILDCLASS = "autotools"

DEPENDS = " \
    libxml2-native \
    glib-2.0-native \
    glib-2.0 \
    libsoup-2.4 \
"

inherit gnomebase gobject-introspection gtk-doc vala

SRC_URI[archive.md5sum] = "ece4547298a81105f307369d73c21b9d"
SRC_URI[archive.sha256sum] = "0513aad38e5d3cedd4ae3c551634e3be1b9baaa79775e53b2dba9456f15b01c9"

# * gnome environment requires libsoup build with in gnome PACKAGECONFIG
# * libsoup-gnome support was removed upstream three years ago [1]
# [1] https://gitlab.gnome.org/GNOME/librest/commit/8f904a8e2bb38a7bf72245cdf2f1ecad17e9a720
EXTRA_OECONF = "--without-gnome"

do_configure_prepend() {
    # rest expects introspection.m4 at custom location (see aclocal.m4).
    cp -f ${STAGING_DIR_TARGET}/${datadir}/aclocal/introspection.m4 ${S}/build
}

do_compile_prepend() {
    export GIR_EXTRA_LIBS_PATH="${B}/rest/.libs"
}

