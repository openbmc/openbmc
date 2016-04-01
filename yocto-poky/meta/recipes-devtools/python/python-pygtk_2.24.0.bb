SUMMARY = "Python bindings for the GTK+ UI toolkit"
SECTION = "devel/python"
# needs gtk+ 2.17.x
DEPENDS = "gtk+ libglade python-pycairo python-pygobject"
RDEPENDS_${PN} = "python-shell python-pycairo python-pygobject"
PROVIDES = "python-pygtk2"
SRCNAME = "pygtk"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=a916467b91076e631dd8edb7424769c7"

PR = "r1"

SRC_URI = "ftp://ftp.gnome.org/pub/gnome/sources/pygtk/2.24/${SRCNAME}-${PV}.tar.bz2 \
           file://add-gtk-types.defs-into-gdk.c-dependence.patch \
           file://fix-gtkunixprint.patch \
           file://prevent_to_get_display_during_import.patch \
           file://nodocs.patch \
	   file://fix-pygtk-2.0.pc.patch \
           file://acinclude.m4 \
           file://update-dependences-of-defs.c.patch"

SRC_URI[md5sum] = "a1051d5794fd7696d3c1af6422d17a49"
SRC_URI[sha256sum] = "cd1c1ea265bd63ff669e92a2d3c2a88eb26bcd9e5363e0f82c896e649f206912"

S = "${WORKDIR}/${SRCNAME}-${PV}"

EXTRA_OECONF = "--disable-docs --with-python-includes=${STAGING_INCDIR}/../"

inherit autotools pkgconfig distutils-base distro_features_check

ANY_OF_DISTRO_FEATURES = "${GTK2DISTROFEATURES}"

do_configure_prepend() {
	install -m 0644 ${WORKDIR}/acinclude.m4 ${S}/
	sed -i \
		-e s:'`$PKG_CONFIG --variable defsdir pygobject-2.0`':\"${STAGING_DATADIR}/pygobject/2.0/defs\":g \
		-e s:'`$PKG_CONFIG --variable=pygtkincludedir pygobject-2.0`':\"${STAGING_INCDIR}/pygtk-2.0\":g \
		-e s:'`$PKG_CONFIG --variable=datadir pygobject-2.0`':\"${STAGING_DATADIR}\":g \
		-e s:'`$PKG_CONFIG --variable codegendir pygobject-2.0`':\"${STAGING_DATADIR}/pygobject/2.0/codegen\":g \
		-e s:'`$PKG_CONFIG --variable=fixxref pygobject-2.0`':\"${STAGING_DATADIR}/pygobject/xsl/fixxref.py\":g \
		${S}/configure.ac
}

# dirty fix #2: fix build system paths leaking in
do_install_append() {
	sed -i -e '1s|^#!.*python|#!/usr/bin/env python|' ${D}${bindir}/pygtk-demo
}

PACKAGES =+ "${PN}-demo"
FILES_${PN}-demo = " ${bindir}/pygtk-demo ${libdir}/pygtk "
RDEPENDS_${PN}-demo = "python-pygtk python-stringold python-lang"

# todo: revamp packaging, package demo seperatly
FILES_${PN}-dev += " ${libdir}/pygtk/2.0 ${bindir}/pygtk-* ${datadir}/pygtk/2.0"

