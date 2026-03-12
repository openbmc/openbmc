SUMMARY = "wolfSSL Lightweight Embedded SSL/TLS Library"
DESCRIPTION = "wolfSSL, formerly CyaSSL, is a lightweight SSL library written \
               in C and optimized for embedded and RTOS environments. It can \
               be up to 20 times smaller than OpenSSL while still supporting \
               a full TLS client and server, up to TLS 1.3"
HOMEPAGE = "https://www.wolfssl.com/products/wolfssl"
BUGTRACKER = "https://github.com/wolfssl/wolfssl/issues"
SECTION = "libs"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

PROVIDES += "cyassl"
RPROVIDES:${PN} = "cyassl"

SRC_URI = " \
    git://github.com/wolfSSL/wolfssl.git;protocol=https;branch=master;tag=v${PV}-stable \
    file://run-ptest \
"

SRCREV = "59f4fa568615396fbf381b073b220d1e8d61e4c2"


inherit autotools ptest

EXTRA_OECONF += "--enable-certreq --enable-dtls --enable-opensslextra --enable-certext --enable-certgen"

PACKAGECONFIG ?= "reproducible-build"

PACKAGECONFIG[reproducible-build] = "--enable-reproducible-build,--disable-reproducible-build,"
BBCLASSEXTEND += "native nativesdk"

CFLAGS += '-fPIC -DCERT_REL_PREFIX=\\"./\\"'

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

CVE_STATUS[CVE-2025-11931] = "fixed-version: The currently used version (5.8.4) contains the fix already."
CVE_STATUS[CVE-2025-12889] = "fixed-version: The currently used version (5.8.4) contains the fix already."
