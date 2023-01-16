SUMMARY = "The module provides handy URL class for url parsing and changing"
HOMEPAGE = "https://github.com/aio-libs/yarl/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e581798a7b985311f29fa3e163ea27ae"

SRC_URI[sha256sum] = "49d43402c6e3013ad0978602bf6bf5328535c48d192304b91b97a3c6790b1562"

SRC_URI += "file://run-ptest"

PYPI_PACKAGE = "yarl"

inherit pypi ptest setuptools3

RDEPENDS:${PN} = "\
    ${PYTHON_PN}-multidict \
    ${PYTHON_PN}-idna \
    ${PYTHON_PN}-io \
"

RDEPENDS:${PN}-ptest += " \
    ${PYTHON_PN}-pytest \
"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}
