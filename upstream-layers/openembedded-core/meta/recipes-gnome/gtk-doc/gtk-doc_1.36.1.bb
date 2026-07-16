SUMMARY = "Documentation generator for glib-based software"
DESCRIPTION = "Gtk-doc is a set of scripts that extract specially formatted comments \
               from glib-based software and produce a set of html documentation files from them"
HOMEPAGE = "https://www.gtk.org/docs/"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

inherit gnomebase

DEPENDS += "libxslt-native"

PACKAGECONFIG ??= ""

PACKAGECONFIG[tests] = "-Dtests=true,-Dtests=false,glib-2.0"

EXTRA_OEMESON = "-Dyelp_manual=false -Dcheck_runtime_deps=false"

SRC_URI[archive.sha256sum] = "0e517a5f97069831181be177516bde8aa8b3922398f2bdb09e265d22aecadbc5"
SRC_URI += "file://0001-Do-not-hardocode-paths-to-perl-python-in-scripts.patch \
           file://no-clobber.patch \
           file://0001-meson.build-add-an-option-to-not-check-for-runtime-d.patch \
           "
SRC_URI:append:class-native = " file://pkg-config-native.patch"

BBCLASSEXTEND = "native nativesdk"

do_install:append () {
    # configure values for python3 and pkg-config encoded in scripts
    for fn in ${bindir}/gtkdoc-depscan \
        ${bindir}/gtkdoc-mkhtml2 \
        ${datadir}/gtk-doc/python/gtkdoc/config_data.py \
        ${datadir}/gtk-doc/python/gtkdoc/config.py; do
        sed -e 's,${RECIPE_SYSROOT_NATIVE}/usr/bin/,,' \
            -e 's,${HOSTTOOLS_DIR}/python3,${bindir}/python3,' \
            -e '1s|^#!.*|#!/usr/bin/env python3|' \
            -i ${D}$fn
    done
}

SYSROOT_PREPROCESS_FUNCS:append:class-native = " gtkdoc_makefiles_sysroot_preprocess"
gtkdoc_makefiles_sysroot_preprocess() {
        # Patch the gtk-doc makefiles so that the qemu wrapper is used to run transient binaries
        # instead of libtool wrapper or running them directly
        sed -i \
           -e "s|GTKDOC_RUN =.*|GTKDOC_RUN = \$(top_builddir)/gtkdoc-qemuwrapper|" \
           ${SYSROOT_DESTDIR}${datadir}/gtk-doc/data/gtk-doc*make
}
