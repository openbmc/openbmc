SUMMARY = "The module provides handy URL class for url parsing and changing"
HOMEPAGE = "https://github.com/aio-libs/yarl/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b334fc90d45983db318f54fd5bf6c90b"

SRC_URI[md5sum] = "802bb27ebdb260fbbaecbcc8168d6f28"
SRC_URI[sha256sum] = "61d3ea3c175fe45f1498af868879c6ffeb989d4143ac542163c45538ba5ec21b"

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
