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
DEPENDS_append_class-nativesdk = " ${BPN}-native"

inherit autotools-brokensep pkgconfig gettext

SRC_URI = "https://www2.graphviz.org/Packages/stable/portable_source/${BP}.tar.gz \
           file://0001-plugin-pango-Include-freetype-headers-explicitly.patch \
"
# Use native mkdefs
SRC_URI_append_class-target = "\
           file://0001-Use-native-mkdefs.patch \
           file://0001-Set-use_tcl-to-be-empty-string-if-tcl-is-disabled.patch \
"
SRC_URI_append_class-nativesdk = "\
           file://0001-Use-native-mkdefs.patch \
           file://graphviz-setup.sh \
"
SRC_URI[sha256sum] = "8e1b34763254935243ccdb83c6ce108f531876d7a5dfd443f255e6418b8ea313"

EXTRA_OECONF_append = " PS2PDF=/bin/echo"

EXTRA_OECONF_class-target = "\
                --with-expatincludedir=${STAGING_INCDIR} \
                --with-expatlibdir=${STAGING_LIBDIR} \
                --without-included-ltdl \
                --disable-java \
                --disable-tcl \
                --disable-r \
                --disable-sharp \
                "
EXTRA_OECONF_class-nativesdk = "\
                --with-expatincludedir=${STAGING_INCDIR} \ 
                --with-expatlibdir=${STAGING_LIBDIR} \
                --without-included-ltdl \
                --disable-java \
                --disable-tcl \
                --disable-r \
                --disable-sharp \
                "
CFLAGS_append_class-target = " -D_typ_ssize_t=1 -D_long_double=1"
CFLAGS_append_class-nativesdk = " -D_typ_ssize_t=1 -D_long_double=1"
do_configure_prepend() {
    cd ${S}
    # create version.m4 and ignore libtoolize errors
    ./autogen.sh NOCONFIG || true
}

do_install_append_class-native() {
    # install mkdefs for target build
    install -m755 ${B}/lib/gvpr/mkdefs ${D}${bindir}
}

do_install_append_class-nativesdk() {
    # graphviz-setup.sh must be executed at SDK installation
    install -d ${D}${SDKPATHNATIVE}/post-relocate-setup.d
    install -m 0755 ${WORKDIR}/graphviz-setup.sh ${D}${SDKPATHNATIVE}/post-relocate-setup.d
}
FILES_${PN}_class-nativesdk += "${SDKPATHNATIVE}"

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

BBCLASSEXTEND = "native nativesdk"
