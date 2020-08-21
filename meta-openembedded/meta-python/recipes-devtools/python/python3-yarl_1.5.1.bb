SUMMARY = "The module provides handy URL class for url parsing and changing"
HOMEPAGE = "https://github.com/aio-libs/yarl/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b334fc90d45983db318f54fd5bf6c90b"

SRC_URI[md5sum] = "a9b20bf0b8a6962e1101b28908a67bf8"
SRC_URI[sha256sum] = "c22c75b5f394f3d47105045ea551e08a3e804dc7e01b37800ca35b58f856c3d6"

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
