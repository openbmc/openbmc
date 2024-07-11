SUMMARY = "A C implementation of the Constrained Application Protocol"
DESCRIPTION = "libcoap implements a lightweight application-protocol for \
devices that are constrained their resources such as computing power, \
RF range, memory, bandwith, or network packet sizes."
HOMEPAGE ="https://libcoap.net/"

LICENSE = "BSD-2-Clause & BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1978dbc41673ab1c20e64b287c8317bc"

SRC_URI = "git://github.com/obgm/libcoap.git;branch=main;protocol=https \
           file://run-ptest \
           file://CVE-2024-0962.patch \
           "
SRCREV = "5fd2f89ef068214130e5d60b7087ef48711fa615"

S = "${WORKDIR}/git"

inherit autotools manpages pkgconfig ptest

DEPENDS += "ctags-native"

PACKAGECONFIG ?= "\
    async openssl tcp \
    ${@bb.utils.contains('PTEST_ENABLED', '1', 'tests', '', d)} \
"
PACKAGECONFIG[async] = "--enable-async,--disable-async"
PACKAGECONFIG[gnutls] = "--with-gnutls,--without-gnutls,gnutls,,,openssl mbedtls"
PACKAGECONFIG[manpages] = "--enable-documentation --enable-doxygen --enable-manpages,--disable-documentation,asciidoc-native doxygen-native graphviz-native"
PACKAGECONFIG[mbedtls] = "--with-mbedtls,--without-mbedtls,mbedtls,,,gnutls openssl"
PACKAGECONFIG[openssl] = "--with-openssl,--without-openssl,openssl,,,gnutls mbedtls"
PACKAGECONFIG[small-stack] = "--enable-small-stack,--disable-small-stack"
PACKAGECONFIG[tcp] = "--enable-tcp,--disable-tcp"
PACKAGECONFIG[tests] = "--enable-tests,--disable-tests,cunit"

EXTRA_OECONF = "\
    --with-epoll --enable-add-default-names \
    --without-tinydtls --without-submodule-tinydtls \
    ${@bb.utils.contains_any('PACKAGECONFIG', 'gnutls openssl mbedtls', '--enable-dtls', '--disable-dtls', d)} \
"

python () {
    if d.getVar('PTEST_ENABLED') == "1":
        d.setVar('DISABLE_STATIC', '')
}

export SGML_CATALOG_FILES="file://${STAGING_ETCDIR_NATIVE}/xml/catalog"

do_compile:prepend() {
    oe_runmake update-map-file
}

do_install_ptest () {
	install -d ${D}${PTEST_PATH}
	install -m 0755 ${WORKDIR}/run-ptest ${D}${PTEST_PATH}/run-ptest
	install -m 0755 ${B}/tests/testdriver ${D}${PTEST_PATH}/testdriver
}

PACKAGE_BEFORE_PN += "\
    ${PN}-bin \
"

FILES:${PN}-bin = "${bindir}"
FILES:${PN}-dev += "${datadir}/${BPN}/examples"
