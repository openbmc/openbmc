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

SRC_URI = "https://github.com/lsh123/xmlsec/releases/download/${PV}/${BP}.tar.gz \
           file://0001-force-to-use-our-own-libtool.patch \
           file://0002-change-finding-path-of-nss-and-nspr.patch \
           file://0003-xmlsec1-add-new-recipe.patch \
           file://0005-nss-nspr-fix-for-multilib.patch \
           file://0006-xmlsec1-Fix-configure-QA-error-caused-by-host-lookup.patch \
           file://0008-unit_tests-guard-xmlDebugDumpDocument-with-LIBXML_DEB.patch \
           file://run-ptest \
           "

SRC_URI[sha256sum] = "53675e98fa83b48201d24f7bfbcaeaa1b51496b8b19ff969785856bdeb196af3"

UPSTREAM_CHECK_URI = "https://github.com/lsh123/xmlsec/releases"
UPSTREAM_CHECK_REGEX = "releases/tag/(?P<pver>\d+(\.\d+)+)"

inherit autotools-brokensep ptest pkgconfig

#CFLAGS += "-I${STAGING_INCDIR}/nss3"
#CPPFLAGS += "-I${STAGING_INCDIR}/nss3"

PACKAGECONFIG ??= "gnutls libgcrypt nss openssl"
PACKAGECONFIG[gnutls] = ",,gnutls"
PACKAGECONFIG[libgcrypt] = ",,libgcrypt"
PACKAGECONFIG[nss] = "--with-nss=${STAGING_DIR_HOST} --with-nspr=${STAGING_DIR_HOST},--with-nss=no --with-nspr=no,nss nspr"
PACKAGECONFIG[openssl] = ",,openssl"
PACKAGECONFIG[des] = ",--disable-des,,"

# these can be dynamically loaded with xmlSecCryptoDLLoadLibrary()
FILES_SOLIBSDEV = "${libdir}/libxmlsec1.so"
FILES:${PN} += "${libdir}/libxmlsec1-*.so"
INSANE_SKIP:${PN} = "dev-so"

FILES:${PN}-dev += "${libdir}/xmlsec1Conf.sh"
FILES:${PN}-dbg += "${PTEST_PATH}/.debug/*"

RDEPENDS:${PN}-ptest += "${PN}-dev"
INSANE_SKIP:${PN}-ptest += "dev-deps"

# The ptests are not buildable now that pkgconf is being used, disable until fixed.
PTEST_ENABLED = "0"

PTEST_EXTRA_ARGS = "top_srcdir=${S} top_builddir=${B}"

do_compile_ptest () {
    oe_runmake -C ${S}/examples ${PTEST_EXTRA_ARGS} all
}

do_install:append() {
    for i in \
        ${bindir}/xmlsec1-config \
        ${libdir}/xmlsec1Conf.sh \
        ${libdir}/pkgconfig/xmlsec1-openssl.pc \
        ${libdir}/pkgconfig/xmlsec1-gnutls.pc; do
        [ -f ${D}$i ] && sed -i -e "s@${RECIPE_SYSROOT}@@g" ${D}$i || true
    done
}

do_install_ptest () {
    oe_runmake -C ${S}/examples DESTDIR=${D}${PTEST_PATH} ${PTEST_EXTRA_ARGS} install-ptest
}

BBCLASSEXTEND = "native"
