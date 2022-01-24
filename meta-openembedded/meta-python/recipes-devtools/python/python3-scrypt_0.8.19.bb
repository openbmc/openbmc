DESCRIPTION = "Bindings for the scrypt key derivation function library"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=05b5ac2824a7ae7489193b0f6a6f2cd1"
HOMEPAGE="https://github.com/holgern/py-scrypt"

SRC_URI += "file://0001-py-scrypt-remove-the-hard-coded-include-paths.patch"

SRC_URI[sha256sum] = "ad143035ae0cf5e97c4b399f4e4686adf442c5f0f06f9f198a0cc6c091335fb7"

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
