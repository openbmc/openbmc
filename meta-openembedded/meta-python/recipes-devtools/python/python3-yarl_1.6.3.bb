SUMMARY = "The module provides handy URL class for url parsing and changing"
HOMEPAGE = "https://github.com/aio-libs/yarl/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b334fc90d45983db318f54fd5bf6c90b"

SRC_URI[sha256sum] = "8a9066529240171b68893d60dca86a763eae2139dd42f42106b03cf4b426bf10"

SRC_URI += " \
    file://run-ptest \
"

PYPI_PACKAGE = "yarl"

inherit pypi ptest setuptools3

RDEPENDS_${PN} = "\
    ${PYTHON_PN}-multidict \
    ${PYTHON_PN}-idna \
"

RDEPENDS_${PN}-ptest += " \
    ${PYTHON_PN}-pytest \
"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}
