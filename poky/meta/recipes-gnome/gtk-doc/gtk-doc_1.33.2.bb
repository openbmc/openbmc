SUMMARY = "Documentation generator for glib-based software"
DESCRIPTION = "Gtk-doc is a set of scripts that extract specially formatted comments \
               from glib-based software and produce a set of html documentation files from them"
HOMEPAGE = "https://www.gtk.org/docs/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

inherit gnomebase

# Configure the scripts correctly (and build their dependencies) only if they are actually
# going to be used; otheriwse we need only the m4/makefile includes from the gtk-doc tarball.
PACKAGECONFIG ??= "${@bb.utils.contains("DISTRO_FEATURES", "api-documentation", "working-scripts", "", d)}"

# This will cause target gtk-doc to hardcode paths of native dependencies
# into its scripts. This means that target gtk-doc package is broken;
# hopefully no one minds because its scripts are not used for anything during build
# and shouldn't be used on targets.
PACKAGECONFIG[working-scripts] = ",,libxslt-native xmlto-native python3-six python3-pygments"
PACKAGECONFIG[tests] = "--enable-tests,--disable-tests,glib-2.0"

SRC_URI[archive.sha256sum] = "cc1b709a20eb030a278a1f9842a362e00402b7f834ae1df4c1998a723152bf43"
SRC_URI += "file://0001-Do-not-hardocode-paths-to-perl-python-in-scripts.patch \
           file://0001-Do-not-error-out-if-xsltproc-is-not-found.patch \
           file://conditionaltests.patch \
           file://no-clobber.patch \
           "
SRC_URI_append_class-native = " file://pkg-config-native.patch"

BBCLASSEXTEND = "native nativesdk"

# Do not check for XML catalogs when building because that
# information is not used for anything during build. Recipe
# dependencies make sure we have all the right bits.
do_configure_prepend() {
        sed -i -e 's,^JH_CHECK_XML_CATALOG.*,,' ${S}/configure.ac
}

do_install_append () {
    # configure values for python3 and pkg-config encoded in scripts
    for fn in ${bindir}/gtkdoc-depscan \
        ${bindir}/gtkdoc-mkhtml2 \
        ${datadir}/gtk-doc/python/gtkdoc/config_data.py \
        ${datadir}/gtk-doc/python/gtkdoc/config.py; do
        sed -e 's,${RECIPE_SYSROOT_NATIVE}/usr/bin/pkg-config,${bindir}/pkg-config,' \
            -e 's,${HOSTTOOLS_DIR}/python3,${bindir}/python3,' \
            -i ${D}$fn
    done
}

FILES_${PN} += "${datadir}/sgml"
FILES_${PN}-dev += "${libdir}/cmake"
FILES_${PN}-doc = ""

SYSROOT_PREPROCESS_FUNCS_append_class-native = " gtkdoc_makefiles_sysroot_preprocess"
gtkdoc_makefiles_sysroot_preprocess() {
        # Patch the gtk-doc makefiles so that the qemu wrapper is used to run transient binaries
        # instead of libtool wrapper or running them directly
        sed -i \
           -e "s|GTKDOC_RUN =.*|GTKDOC_RUN = \$(top_builddir)/gtkdoc-qemuwrapper|" \
           ${SYSROOT_DESTDIR}${datadir}/gtk-doc/data/gtk-doc*make
}
