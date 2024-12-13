SUMMARY = "Graph Visualization Tools"
HOMEPAGE = "http://www.graphviz.org"
LICENSE = "EPL-1.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=9109f5fc16cf963fb3cdd32781b3ce04"

DEPENDS = " \
    bison-native \
    groff-native \
    libtool \
    gdk-pixbuf \
    cairo \
    pango \
    expat \
    freetype \
"
DEPENDS:append:class-target = " ${BPN}-native"
DEPENDS:append:class-nativesdk = " ${BPN}-native"

inherit autotools-brokensep pkgconfig gettext qemu

SRC_URI = "https://gitlab.com/api/v4/projects/4207231/packages/generic/${BPN}-releases/${PV}/${BP}.tar.xz \
           file://0001-Autotools-fix-do-not-put-prefix-based-paths-in-compi.patch \
           "
# Use native mkdefs
SRC_URI:append:class-target = "\
           file://0001-Set-use_tcl-to-be-empty-string-if-tcl-is-disabled.patch \
"
SRC_URI:append:class-nativesdk = "\
           file://graphviz-setup.sh \
"
SRC_URI[sha256sum] = "d593695fdaa8a19297523b679ad13d3ef2027b0b7f14cc2bc23e77969ed81565"

UPSTREAM_CHECK_URI = "https://graphviz.org/download/"
UPSTREAM_CHECK_REGEX = "(?P<pver>\d+(\.\d+)+)"

PACKAGECONFIG ??= "librsvg"
PACKAGECONFIG[librsvg] = "--with-librsvg,--without-librsvg,librsvg"

EXTRA_OECONF:append = " PS2PDF=/bin/echo --disable-man-pdfs"

EXTRA_OECONF:class-target = "\
                --with-expatincludedir=${STAGING_INCDIR} \
                --with-expatlibdir=${STAGING_LIBDIR} \
                --without-included-ltdl \
                --disable-java \
                --disable-tcl \
                --disable-r \
                --disable-sharp \
                "
EXTRA_OECONF:class-nativesdk = "\
                --with-expatincludedir=${STAGING_INCDIR} \ 
                --with-expatlibdir=${STAGING_LIBDIR} \
                --without-included-ltdl \
                --disable-java \
                --disable-tcl \
                --disable-r \
                --disable-sharp \
                "
EXTRA_OECONF:class-native = "\
                --disable-tcl \
                "
CFLAGS:append:class-target = " -D_typ_ssize_t=1 -D_long_double=1"
CFLAGS:append:class-nativesdk = " -D_typ_ssize_t=1 -D_long_double=1"
do_configure:prepend() {
    cd ${S}
    # create version.m4 and ignore libtoolize errors
    ./autogen.sh NOCONFIG || true
}

do_install:append:class-nativesdk() {
    # graphviz-setup.sh must be executed at SDK installation
    install -d ${D}${SDKPATHNATIVE}/post-relocate-setup.d
    install -m 0755 ${UNPACKDIR}/graphviz-setup.sh ${D}${SDKPATHNATIVE}/post-relocate-setup.d
}
FILES:${PN}:class-nativesdk += "${SDKPATHNATIVE}"

# create /usr/lib/graphviz/config6
graphviz_sstate_postinst() {
    mkdir -p ${SYSROOT_DESTDIR}${bindir}
    dest=${SYSROOT_DESTDIR}${bindir}/postinst-${PN}
    echo '#!/bin/sh' > $dest
    echo '' >> $dest
    echo 'dot -c' >> $dest
    chmod 0755 $dest
}
SYSROOT_PREPROCESS_FUNCS:append:class-native = " graphviz_sstate_postinst"

pkg_postinst:${PN} () {
    if [ -n "$D" ]; then
        if ${@bb.utils.contains('MACHINE_FEATURES', 'qemu-usermode', 'true', 'false', d)}; then
            ${@qemu_run_binary(d, '$D', '${bindir}/dot')} -c
        fi
    else
        dot -c
    fi
}

pkg_postrm:${PN} () {
    rm -f $D${libdir}/graphviz/config*
    rmdir --ignore-fail-on-non-empty $D${libdir}/graphviz
}

PACKAGE_WRITE_DEPS += "qemu-native"

PACKAGES =+ "${PN}-python ${PN}-perl ${PN}-demo"

FILES:${PN}-python += "${PYTHON_SITEPACKAGES_DIR}/ ${libdir}/graphviz/python/"
FILES:${PN}-perl += "${libdir}/perl5/*/vendor_perl/ ${libdir}/graphviz/perl/"
FILES:${PN}-demo += "${datadir}/graphviz/demo/"

RDEPENDS:${PN}-perl += "perl"
RDEPENDS:${PN}-python += "python3"
RDEPENDS:${PN}-demo += "python3 perl"

RRECOMMENDS:${PN} = "liberation-fonts"

INSANE_SKIP:${PN}-perl = "dev-so"
INSANE_SKIP:${PN}-python = "dev-so"

FILES_SOLIBSDEV:append = " ${libdir}/graphviz/lib*${SOLIBSDEV}"

RRECOMMENDS:${PN} = "liberation-fonts"

BBCLASSEXTEND = "native nativesdk"
