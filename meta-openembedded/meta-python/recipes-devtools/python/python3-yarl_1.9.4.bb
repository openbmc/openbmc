SUMMARY = "The module provides handy URL class for url parsing and changing"
HOMEPAGE = "https://github.com/aio-libs/yarl/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI[sha256sum] = "566db86717cf8080b99b58b083b773a908ae40f06681e87e589a976faf8246bf"

SRC_URI += "file://run-ptest"

PYPI_PACKAGE = "yarl"

inherit pypi ptest python_setuptools_build_meta

DEPENDS += " \
    ${PYTHON_PN}-expandvars-native \
    ${PYTHON_PN}-cython-native \
"

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
