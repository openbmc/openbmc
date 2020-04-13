SUMMARY = "XML Security Library is a C library based on LibXML2"
DESCRIPTION = "\
    XML Security Library is a C library based on \
    LibXML2 and OpenSSL. The library was created with a goal to support major \
    XML security standards "XML Digital Signature" and "XML Encryption". \
    "
HOMEPAGE = "http://www.aleksey.com/xmlsec/"
DEPENDS = "libtool libxml2 libxslt zlib"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=352791d62092ea8104f085042de7f4d0"

SECTION = "libs"

SRC_URI = "http://www.aleksey.com/xmlsec/download/${BP}.tar.gz \
    file://fix-ltmain.sh.patch \
    file://change-finding-path-of-nss.patch \
    file://makefile-ptest.patch \
    file://xmlsec1-examples-allow-build-in-separate-dir.patch \
    file://0001-nss-nspr-fix-for-multilib.patch \
    file://run-ptest \
    "

SRC_URI[md5sum] = "b66ec21e0a0ac331afb4b1bc5c9ef966"
SRC_URI[sha256sum] = "2d84360b03042178def1d9ff538acacaed2b3a27411db7b2874f1612ed71abc8"

inherit autotools-brokensep ptest pkgconfig

#CFLAGS += "-I${STAGING_INCDIR}/nss3"
#CPPFLAGS += "-I${STAGING_INCDIR}/nss3"

PACKAGECONFIG ??= "gnutls libgcrypt nss openssl des"
PACKAGECONFIG[gnutls] = ",,gnutls"
PACKAGECONFIG[libgcrypt] = ",,libgcrypt"
PACKAGECONFIG[nss] = "--with-nss=${STAGING_DIR_HOST} --with-nspr=${STAGING_DIR_HOST},,nss nspr"
PACKAGECONFIG[openssl] = ",,openssl"
PACKAGECONFIG[des] = ",--disable-des,,"

# these can be dynamically loaded with xmlSecCryptoDLLoadLibrary()
FILES_SOLIBSDEV = "${libdir}/libxmlsec1.so"
FILES_${PN} += "${libdir}/libxmlsec1-*.so"
INSANE_SKIP_${PN} = "dev-so"

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
