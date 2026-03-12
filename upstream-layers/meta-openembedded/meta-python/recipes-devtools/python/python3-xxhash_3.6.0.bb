SUMMARY = "xxhash is a Python binding for the xxHash library by Yann Collet."
SECTION = "devel/python"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=82f3295151c5e61043a4a201c031a5ee"

DEPENDS += "python3-setuptools-scm-native"

SRC_URI[sha256sum] = "f0162a78b13a0d7617b2845b90c763339d1f1d82bb04a4b07f4ab535cc5e05d6"

SRC_URI += " \
    file://run-ptest \
"

inherit pypi python_setuptools_build_meta ptest

RDEPENDS:${PN}-ptest += " \
    python3-pytest \
    python3-unittest-automake-output \
"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}
