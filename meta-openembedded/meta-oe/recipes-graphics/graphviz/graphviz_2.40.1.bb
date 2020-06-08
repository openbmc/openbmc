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

# The source tarball suggested at
# https://graphviz.gitlab.io/_pages/Download/Download_source.html has no
# version in its name. So once graphviz is updgraded, only first time users will
# get checksum errors. Fedora people seem to expect same so they use a versioned
# source - see https://src.fedoraproject.org/cgit/rpms/graphviz.git/tree/graphviz.spec

SRC_URI = "https://gitlab.com/graphviz/graphviz/-/archive/stable_release_${PV}/graphviz-stable_release_${PV}.tar.gz \
           file://0001-plugin-pango-Include-freetype-headers-explicitly.patch \
"
# Use native mkdefs
SRC_URI_append_class-target = "\
           file://0001-Use-native-mkdefs.patch \
           file://0001-Set-use_tcl-to-be-empty-string-if-tcl-is-disabled.patch \
"
SRC_URI[md5sum] = "2acf30ca8e6cc8b001b0334db65fd072"
SRC_URI[sha256sum] = "e6c3f8dbfde1c4523055403927bef29f97f9fc12715c1042b5dcf648a2c1c62a"

S = "${WORKDIR}/${BPN}-stable_release_${PV}"

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
