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

SRCREV = "a82476d144290bf6a786607a16c224acff63d882"


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

    # Remove symlinks pointing to build directory (symlink-to-sysroot QA)
    find ${D}${PTEST_PATH} -type l -delete
}

CVE_STATUS[CVE-2026-0819] = "fixed-version: fixed in 5.9.0"
CVE_STATUS[CVE-2026-2646] = "fixed-version: fixed in 5.9.0"
CVE_STATUS[CVE-2026-3503] = "fixed-version: fixed in 5.9.0"
CVE_STATUS[CVE-2026-3548] = "fixed-version: fixed in 5.9.0"
CVE_STATUS[CVE-2026-5188] = "fixed-version: fixed in 5.9.1"
CVE_STATUS[CVE-2026-5194] = "fixed-version: fixed in 5.9.1"
CVE_STATUS[CVE-2026-5263] = "fixed-version: fixed in 5.9.1"
CVE_STATUS[CVE-2026-5264] = "fixed-version: fixed in 5.9.1"
CVE_STATUS[CVE-2026-5392] = "fixed-version: fixed in 5.9.1"
CVE_STATUS[CVE-2026-5393] = "fixed-version: fixed in 5.9.1"
CVE_STATUS[CVE-2026-5446] = "fixed-version: fixed in 5.9.1"
CVE_STATUS[CVE-2026-5447] = "fixed-version: fixed in 5.9.1"
CVE_STATUS[CVE-2026-5448] = "fixed-version: fixed in 5.9.1"
CVE_STATUS[CVE-2026-5460] = "fixed-version: fixed in 5.9.1"
CVE_STATUS[CVE-2026-5466] = "fixed-version: fixed in 5.9.1"
CVE_STATUS[CVE-2026-5479] = "fixed-version: fixed in 5.9.1"
CVE_STATUS[CVE-2026-5500] = "fixed-version: fixed in 5.9.1"
CVE_STATUS[CVE-2026-5503] = "fixed-version: fixed in 5.9.1"
CVE_STATUS[CVE-2026-5504] = "fixed-version: fixed in 5.9.1"
CVE_STATUS[CVE-2026-5507] = "fixed-version: fixed in 5.9.1"
CVE_STATUS[CVE-2026-5772] = "fixed-version: fixed in 5.9.1"
CVE_STATUS[CVE-2026-5778] = "fixed-version: fixed in 5.9.1"
