SUMMARY = "wolfSSL Lightweight Embedded SSL/TLS Library"
DESCRIPTION = "wolfSSL, formerly CyaSSL, is a lightweight SSL library written \
               in C and optimized for embedded and RTOS environments. It can \
               be up to 20 times smaller than OpenSSL while still supporting \
               a full TLS client and server, up to TLS 1.3"
HOMEPAGE = "https://www.wolfssl.com/products/wolfssl"
BUGTRACKER = "https://github.com/wolfssl/wolfssl/issues"
SECTION = "libs"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

PROVIDES += "cyassl"
RPROVIDES:${PN} = "cyassl"

SRC_URI = " \
    git://github.com/wolfSSL/wolfssl.git;protocol=https;branch=master \
    file://run-ptest \
"
SRCREV = "00e42151ca061463ba6a95adb2290f678cbca472"

S = "${WORKDIR}/git"

inherit autotools ptest

PACKAGECONFIG ?= "reproducible-build"

PACKAGECONFIG[reproducible-build] = "--enable-reproducible-build,--disable-reproducible-build,"
BBCLASSEXTEND += "native nativesdk"

RDEPENDS:${PN}-ptest += " bash"

do_install_ptest() {
    # Prevent QA Error "package contains reference to TMPDIR [buildpaths]" for unit.test script
    # Replace the occurences of ${B}/src with '${PTEST_PATH}'
    sed -i 's|${B}/src|${PTEST_PATH}|g' ${B}/tests/unit.test

    install -d ${D}${PTEST_PATH}/test

    # create an empty folder examples, needed in wolfssl's tests/api.c to "Test loading path with no files"
    install -d ${D}${PTEST_PATH}/examples
    cp -rf ${B}/tests/. ${D}${PTEST_PATH}/test
    cp -rf ${S}/certs  ${D}${PTEST_PATH}
    cp -rf ${S}/tests  ${D}${PTEST_PATH}
}
