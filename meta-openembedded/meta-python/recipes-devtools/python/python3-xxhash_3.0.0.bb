SUMMARY = "xxhash is a Python binding for the xxHash library by Yann Collet."
SECTION = "devel/python"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3a9dab7f159514231249aa3e108ab26b"

SRC_URI[sha256sum] = "30b2d97aaf11fb122023f6b44ebb97c6955e9e00d7461a96415ca030b5ceb9c7"

SRC_URI += " \
    file://run-ptest \
"

inherit pypi setuptools3 ptest

RDEPENDS:${PN}-ptest += " \
    ${PYTHON_PN}-pytest \
"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}
