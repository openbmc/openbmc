DESCRIPTION = "Bindings for the scrypt key derivation function library"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2ddf47d0ff1849bce3257ccbc1fd275c"
HOMEPAGE="https://github.com/holgern/py-scrypt"

SRC_URI += "file://0001-py-scrypt-remove-the-hard-coded-include-paths.patch"

SRC_URI[sha256sum] = "25b5075f2238be93af1cd574540a5ea01b8547f9b678aa72d22fce22577475ec"

inherit pypi ptest setuptools3 dos2unix

SRC_URI += " \
    file://run-ptest \
"

RDEPENDS_${PN}-ptest += " \
    ${PYTHON_PN}-pytest \
"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/scrypt/tests/* ${D}${PTEST_PATH}/tests/
}
