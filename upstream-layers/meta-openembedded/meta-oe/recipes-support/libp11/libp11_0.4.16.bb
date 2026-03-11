SUMMARY = "Library for using PKCS"
DESCRIPTION = "\
Libp11 is a library implementing a small layer on top of PKCS \
make using PKCS"
HOMEPAGE = "https://github.com/OpenSC/libp11"
BUGTRACKER = "https://github.com/OpenSC/libp11/issues"
SECTION = "Development/Libraries"
LICENSE = "LGPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=fad9b3332be894bab9bc501572864b29"
DEPENDS = "libtool openssl"

SRC_URI = "git://github.com/OpenSC/libp11.git;branch=master;protocol=https"

SRCREV = "b9c2de288833e38a391ee3cb106f965a40153629"

UPSTREAM_CHECK_GITTAGREGEX = "libp11-(?P<pver>\d+(\.\d+)+)"


inherit autotools pkgconfig

EXTRA_OECONF = "--disable-static"
EXTRA_OECONF:append:class-native = "\
    --with-enginesdir=${RECIPE_SYSROOT_NATIVE}/usr/lib/engines-3 \
    --with-modulesdir=${RECIPE_SYSROOT_NATIVE}/usr/lib/ossl-modules \
"

do_install:append () {
    rm -rf ${D}${docdir}/${BPN}
}

FILES:${PN} += "\
    ${libdir}/engines*/pkcs11.so \
    ${libdir}/ossl-modules/pkcs11prov.so \
"
FILES:${PN}-dev += "\
    ${libdir}/engines*/libpkcs11${SOLIBSDEV} \
    ${libdir}/ossl-modules/libpkcs11${SOLIBSDEV} \
"

BBCLASSEXTEND = "native"
