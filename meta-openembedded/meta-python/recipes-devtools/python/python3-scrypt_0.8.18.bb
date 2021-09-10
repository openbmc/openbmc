DESCRIPTION = "Bindings for the scrypt key derivation function library"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2ddf47d0ff1849bce3257ccbc1fd275c"
HOMEPAGE="https://github.com/holgern/py-scrypt"

SRC_URI += "file://0001-py-scrypt-remove-the-hard-coded-include-paths.patch"

SRC_URI[sha256sum] = "bcf04257af12e6d52974d177a7b08e314b66f350a73f9b6f7b232d69a6a1e041"

inherit pypi ptest setuptools3 dos2unix

SRC_URI += " \
    file://run-ptest \
"

RDEPENDS:${PN}-ptest += " \
    ${PYTHON_PN}-pytest \
"

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-ctypes \
"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/scrypt/tests/* ${D}${PTEST_PATH}/tests/
}
