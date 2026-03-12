SUMMARY = "A C implementation of the Constrained Application Protocol"
DESCRIPTION = "libcoap implements a lightweight application-protocol for \
devices that are constrained their resources such as computing power, \
RF range, memory, bandwith, or network packet sizes."
HOMEPAGE = "https://libcoap.net/"

LICENSE = "BSD-2-Clause & BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=05d17535846895e23ea4c79b16a9e904"

SRC_URI = "git://github.com/obgm/libcoap.git;branch=release-4.3.5-patches;protocol=https;tag=v${PV} \
           file://run-ptest \
           "
SRCREV = "e3fdcdcfbd1588754fe9dd4b754ac9397260f0f9"

# patch releases often use alphabetical suffixes
CVE_VERSION_SUFFIX = "alphabetical"

inherit autotools manpages pkgconfig ptest

DEPENDS += "ctags-native"

PACKAGECONFIG ?= "\
    async openssl tcp \
    ${@bb.utils.contains('PTEST_ENABLED', '1', 'tests', '', d)} \
"
PACKAGECONFIG[async] = "--enable-async,--disable-async"
PACKAGECONFIG[gnutls] = "--with-gnutls,--without-gnutls,gnutls,,,openssl mbedtls wolfssl"
PACKAGECONFIG[manpages] = "--enable-documentation --enable-doxygen --enable-manpages,--disable-documentation,asciidoc-native doxygen-native graphviz-native"
PACKAGECONFIG[mbedtls] = "--with-mbedtls,--without-mbedtls,mbedtls,,,gnutls openssl wolfssl"
PACKAGECONFIG[openssl] = "--with-openssl,--without-openssl,openssl,,,gnutls mbedtls wolfssl"
PACKAGECONFIG[small-stack] = "--enable-small-stack,--disable-small-stack"
PACKAGECONFIG[tcp] = "--enable-tcp,--disable-tcp"
PACKAGECONFIG[tests] = "--enable-tests,--disable-tests,cunit"
PACKAGECONFIG[wolfssl] = "--with-wolfssl,--without-wolfssl,wolfssl,,,gnutls mbedtls openssl"

EXTRA_OECONF = "\
    --with-epoll --enable-add-default-names \
    --without-tinydtls --without-submodule-tinydtls \
    ${@bb.utils.contains_any('PACKAGECONFIG', 'gnutls openssl mbedtls', '--enable-dtls', '--disable-dtls', d)} \
"

python () {
    if d.getVar('PTEST_ENABLED') == "1":
        d.setVar('DISABLE_STATIC', '')
}

export SGML_CATALOG_FILES = "file://${STAGING_ETCDIR_NATIVE}/xml/catalog"

do_compile:prepend() {
    oe_runmake update-map-file
}

do_install_ptest () {
	install -d ${D}${PTEST_PATH}
	install -m 0755 ${UNPACKDIR}/run-ptest ${D}${PTEST_PATH}/run-ptest
	install -m 0755 ${B}/tests/testdriver ${D}${PTEST_PATH}/testdriver
}

PACKAGE_BEFORE_PN += "\
    ${PN}-bin \
"

FILES:${PN}-bin = "${bindir}"
FILES:${PN}-dev += "${datadir}/${BPN}/examples"

CVE_STATUS[CVE-2025-50518] = "disputed: happens only when library is used incorrectly"
