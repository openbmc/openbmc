SUMMARY = "Snowball compiler and stemming algorithms"
HOMEPAGE = "https://snowballstem.org/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=2750797da77c1d784e7626b3f7d7ff3e"

DEPENDS_class-target = "${BPN}-native"

SRC_URI = "\
    git://github.com/snowballstem/snowball.git \
    file://0001-Build-so-lib.patch \
    file://0002-snowball-stemwords-do-link-with-LDFLAGS-set-by-build.patch \
"
SRCREV = "c70ed64f9d41c1032fba4e962b054f8e9d489a74"
S = "${WORKDIR}/git"
PV = "2.0.0"
LIBVER = "0.0.0"

inherit lib_package

BBCLASSEXTEND = "native"

do_compile_prepend_class-target() {
    # use native tools
    sed -i 's:./snowball :snowball :g' ${S}/GNUmakefile
}

do_install() {
    install -d ${D}${bindir}
    install -m 755 ${S}/snowball ${D}${bindir}
    install -m 755 ${S}/stemwords ${D}${bindir}

    install -d ${D}${libdir}
    install -m 755 ${S}/libstemmer.so.${LIBVER} ${D}${libdir}/
    ln -s libstemmer.so.${LIBVER} ${D}${libdir}/libstemmer.so.0
    ln -s libstemmer.so.${LIBVER} ${D}${libdir}/libstemmer.so

    install -d ${D}${includedir}
	install -m 644 ${S}/include/*.h ${D}${includedir}
}
