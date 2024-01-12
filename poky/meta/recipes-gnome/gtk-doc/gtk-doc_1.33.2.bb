SUMMARY = "Documentation generator for glib-based software"
DESCRIPTION = "Gtk-doc is a set of scripts that extract specially formatted comments \
               from glib-based software and produce a set of html documentation files from them"
HOMEPAGE = "https://www.gtk.org/docs/"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

GNOMEBASEBUILDCLASS = "autotools"
inherit gnomebase

# Configure the scripts correctly (and build their dependencies) only if they are actually
# going to be used; otheriwse we need only the m4/makefile includes from the gtk-doc tarball.
PACKAGECONFIG ??= "${@bb.utils.contains("DISTRO_FEATURES", "api-documentation", "working-scripts", "", d)}"

# This will cause target gtk-doc to hardcode paths of native dependencies
# into its scripts. This means that target gtk-doc package is broken;
# hopefully no one minds because its scripts are not used for anything during build
# and shouldn't be used on targets.
PACKAGECONFIG[working-scripts] = ",,libxslt-native docbook-xml-dtd4-native docbook-xsl-stylesheets python3-pygments"
PACKAGECONFIG[tests] = "--enable-tests,--disable-tests,glib-2.0"

CACHED_CONFIGUREVARS += "ac_cv_path_XSLTPROC=xsltproc"

SRC_URI[archive.sha256sum] = "cc1b709a20eb030a278a1f9842a362e00402b7f834ae1df4c1998a723152bf43"
SRC_URI += "file://0001-Do-not-hardocode-paths-to-perl-python-in-scripts.patch \
           file://0001-Do-not-error-out-if-xsltproc-is-not-found.patch \
           file://conditionaltests.patch \
           file://no-clobber.patch \
           file://0001-Don-t-use-docdir-from-environment.patch \
           "
SRC_URI:append:class-native = " file://pkg-config-native.patch"

BBCLASSEXTEND = "native nativesdk"

# Do not check for XML catalogs when building because that
# information is not used for anything during build. Recipe
# dependencies make sure we have all the right bits.
do_configure:prepend() {
        sed -i -e 's,^JH_CHECK_XML_CATALOG.*,,' ${S}/configure.ac
}

do_install:append () {
    # configure values for python3 and pkg-config encoded in scripts
    for fn in ${bindir}/gtkdoc-depscan \
        ${bindir}/gtkdoc-mkhtml2 \
        ${datadir}/gtk-doc/python/gtkdoc/config_data.py \
        ${datadir}/gtk-doc/python/gtkdoc/config.py; do
        sed -e 's,${RECIPE_SYSROOT_NATIVE}/usr/bin/pkg-config,${bindir}/pkg-config,' \
            -e 's,${HOSTTOOLS_DIR}/python3,${bindir}/python3,' \
            -e '1s|^#!.*|#!/usr/bin/env python3|' \
            -i ${D}$fn
    done
}

FILES:${PN} += "${datadir}/sgml"
FILES:${PN}-doc = ""

SYSROOT_PREPROCESS_FUNCS:append:class-native = " gtkdoc_makefiles_sysroot_preprocess"
gtkdoc_makefiles_sysroot_preprocess() {
        # Patch the gtk-doc makefiles so that the qemu wrapper is used to run transient binaries
        # instead of libtool wrapper or running them directly
        sed -i \
           -e "s|GTKDOC_RUN =.*|GTKDOC_RUN = \$(top_builddir)/gtkdoc-qemuwrapper|" \
           ${SYSROOT_DESTDIR}${datadir}/gtk-doc/data/gtk-doc*make
}
