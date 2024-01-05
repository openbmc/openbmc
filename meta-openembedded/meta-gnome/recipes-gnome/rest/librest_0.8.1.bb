SUMMARY = "library to access web services that claim to be "RESTful""
HOMEPAGE = "https://wiki.gnome.org/Projects/Librest"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=2d5025d4aa3495befef8f17206a5b0a1"

DEPENDS = " \
    glib-2.0 \
    glib-2.0-native \
    libsoup-2.4 \
    libxml2 \
"

GNOMEBASEBUILDCLASS = "autotools"
inherit gnomebase gobject-introspection vala pkgconfig gtk-doc

GNOMEBN = "rest"

SRC_URI[archive.sha256sum] = "0513aad38e5d3cedd4ae3c551634e3be1b9baaa79775e53b2dba9456f15b01c9"

S = "${WORKDIR}/${GNOMEBN}-${PV}"
# * gnome environment requires libsoup build with in gnome PACKAGECONFIG
# * libsoup-gnome support was removed upstream three years ago [1]
# [1] https://gitlab.gnome.org/GNOME/librest/commit/8f904a8e2bb38a7bf72245cdf2f1ecad17e9a720
EXTRA_OECONF = "--without-gnome"

do_configure:prepend() {
    # rest expects introspection.m4 at custom location (see aclocal.m4).
    cp -f ${STAGING_DIR_TARGET}/${datadir}/aclocal/introspection.m4 ${S}/build
}

do_compile:prepend() {
    export GIR_EXTRA_LIBS_PATH="${B}/rest/.libs"
}
