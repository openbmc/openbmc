SUMMARY = "XML Security Library is a C library based on LibXML2"
DESCRIPTION = "\
    XML Security Library is a C library based on \
    LibXML2 and OpenSSL. The library was created with a goal to support major \
    XML security standards "XML Digital Signature" and "XML Encryption". \
    "
HOMEPAGE = "http://www.aleksey.com/xmlsec/"
DEPENDS = "libtool libxml2 libxslt openssl zlib libgcrypt gnutls nss nspr libgpg-error"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=352791d62092ea8104f085042de7f4d0"

SECTION = "libs"

SRC_URI = "http://www.aleksey.com/xmlsec/download/${BP}.tar.gz \
    file://fix-ltmain.sh.patch \
    file://change-finding-path-of-nss.patch \
    file://makefile-ptest.patch \
    file://xmlsec1-examples-allow-build-in-separate-dir.patch \
    file://run-ptest \
    "

SRC_URI[md5sum] = "9c4aaf9ff615a73921b9e3bf4988d878"
SRC_URI[sha256sum] = "8d8276c9c720ca42a3b0023df8b7ae41a2d6c5f9aa8d20ed1672d84cc8982d50"

inherit autotools-brokensep ptest pkgconfig

CFLAGS += "-I${STAGING_INCDIR}/nspr4 -I${STAGING_INCDIR}/nss3"
CPPFLAGS += "-I${STAGING_INCDIR}/nspr4 -I${STAGING_INCDIR}/nss3"

EXTRA_OECONF = "\
    --with-nss=${STAGING_LIBDIR}/../.. --with-nspr=${STAGING_LIBDIR}/../.. \
    "

FILES_${PN}-dev += "${libdir}/xmlsec1Conf.sh"
FILES_${PN}-dbg += "${PTEST_PATH}/.debug/*"

RDEPENDS_${PN}-ptest += "${PN}-dev"
INSANE_SKIP_${PN}-ptest += "dev-deps"

PTEST_EXTRA_ARGS = "top_srcdir=${S} top_builddir=${B}"

do_compile_ptest () {
    oe_runmake -C ${S}/examples ${PTEST_EXTRA_ARGS} all
}

do_install_append() {
    for i in ${bindir}/xmlsec1-config ${libdir}/xmlsec1Conf.sh \
        ${libdir}/pkgconfig/xmlsec1-openssl.pc; do
        sed -i -e "s@${RECIPE_SYSROOT}@@g" ${D}$i
    done
}

do_install_ptest () {
    oe_runmake -C ${S}/examples DESTDIR=${D}${PTEST_PATH} ${PTEST_EXTRA_ARGS} install-ptest
}
