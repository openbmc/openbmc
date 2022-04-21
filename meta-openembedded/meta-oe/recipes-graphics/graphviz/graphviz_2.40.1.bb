SUMMARY = "Graph Visualization Tools"
HOMEPAGE = "http://www.graphviz.org"
LICENSE = "EPL-1.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=9109f5fc16cf963fb3cdd32781b3ce04"

DEPENDS = " \
    intltool-native \
    bison-native \
    groff-native \
    libtool \
    gdk-pixbuf \
    librsvg \
    cairo \
    pango \
    expat \
    freetype \
"
DEPENDS_append_class-target = " ${BPN}-native"

inherit autotools-brokensep pkgconfig gettext

# it was already moved from github.com/ellson/graphviz to https://gitlab.com/graphviz/graphviz/
# but the later doesn't have stable_release_2.40.1 tag (anymore?), but it has corresponding commit:
# https://github.com/ellson/MOTHBALLED-graphviz/releases/tag/stable_release_2.40.1
# https://gitlab.com/graphviz/graphviz/-/commit/67cd2e5121379a38e0801cc05cce5033f8a2a609
SRCREV = "67cd2e5121379a38e0801cc05cce5033f8a2a609"
SRC_URI = "git://gitlab.com/${BPN}/${BPN}.git;branch=master \
           file://0001-plugin-pango-Include-freetype-headers-explicitly.patch \
"
# Use native mkdefs
SRC_URI_append_class-target = "\
           file://0001-Use-native-mkdefs.patch \
           file://0001-Set-use_tcl-to-be-empty-string-if-tcl-is-disabled.patch \
"
S = "${WORKDIR}/git"

EXTRA_OECONF_class-target = "\
                --with-expatincludedir=${STAGING_INCDIR} \
                --with-expatlibdir=${STAGING_LIBDIR} \
                --without-included-ltdl \
                --disable-java \
                --disable-tcl \
                --disable-r \
                --disable-sharp \
                "
CFLAGS_append_class-target = " -D_typ_ssize_t=1 -D_long_double=1"
do_configure_prepend() {
    cd ${S}
    # create version.m4 and ignore libtoolize errors
    ./autogen.sh NOCONFIG || true
}

do_install_append_class-native() {
    # install mkdefs for target build
    install -m755 ${B}/lib/gvpr/mkdefs ${D}${bindir}
}

# create /usr/lib/graphviz/config6
graphviz_sstate_postinst() {
    mkdir -p ${SYSROOT_DESTDIR}${bindir}
    dest=${SYSROOT_DESTDIR}${bindir}/postinst-${PN}
    echo '#!/bin/sh' > $dest
    echo '' >> $dest
    echo 'dot -c' >> $dest
    chmod 0755 $dest
}
SYSROOT_PREPROCESS_FUNCS_append_class-native = " graphviz_sstate_postinst"

PACKAGES =+ "${PN}-python ${PN}-perl ${PN}-demo"

FILES_${PN}-python += "${libdir}/python*/site-packages/ ${libdir}/graphviz/python/"
FILES_${PN}-perl += "${libdir}/perl5/*/vendor_perl/ ${libdir}/graphviz/perl/"
FILES_${PN}-demo += "${datadir}/graphviz/demo/"

RDEPENDS_${PN}-perl += "perl"
RDEPENDS_${PN}-python += "python3"
RDEPENDS_${PN}-demo += "python3 perl"

INSANE_SKIP_${PN}-perl = "dev-so"
INSANE_SKIP_${PN}-python = "dev-so"

FILES_SOLIBSDEV_append = " ${libdir}/graphviz/lib*${SOLIBSDEV}"

BBCLASSEXTEND = "native"
