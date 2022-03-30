SUMMARY = "library to access web services that claim to be "RESTful""
HOMEPAGE = "https://wiki.gnome.org/Projects/Librest"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=2d5025d4aa3495befef8f17206a5b0a1"

GNOMEBASEBUILDCLASS = "autotools"

DEPENDS = " \
    libxml2-native \
    glib-2.0-native \
    glib-2.0 \
    libsoup-2.4 \
"

inherit gnomebase gobject-introspection gtk-doc vala

PV .= "+git${SRCPV}"
SRCREV = "7b46065dea860ef09861f4d70124728b8270c8b7"
SRC_URI = "git://gitlab.gnome.org/GNOME/librest;protocol=https;branch=master \
    file://0001-Use-GUri-instead-of-SoupURI.patch \
    file://0002-Port-to-libsoup3.patch \
"
S = "${WORKDIR}/git"

do_configure:prepend() {
    # rest expects introspection.m4 at custom location (see aclocal.m4).
    cp -f ${STAGING_DIR_TARGET}/${datadir}/aclocal/introspection.m4 ${S}/build
}

do_compile:prepend() {
    export GIR_EXTRA_LIBS_PATH="${B}/rest/.libs"
}

