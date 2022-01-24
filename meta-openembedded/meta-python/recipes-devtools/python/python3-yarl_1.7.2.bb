SUMMARY = "The module provides handy URL class for url parsing and changing"
HOMEPAGE = "https://github.com/aio-libs/yarl/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e581798a7b985311f29fa3e163ea27ae"

SRC_URI[sha256sum] = "45399b46d60c253327a460e99856752009fcee5f5d3c80b2f7c0cae1c38d56dd"

SRC_URI += "file://run-ptest"

PYPI_PACKAGE = "yarl"

inherit pypi ptest setuptools3

RDEPENDS:${PN} = "\
    ${PYTHON_PN}-multidict \
    ${PYTHON_PN}-idna \
"

RDEPENDS:${PN}-ptest += " \
    ${PYTHON_PN}-pytest \
"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}
