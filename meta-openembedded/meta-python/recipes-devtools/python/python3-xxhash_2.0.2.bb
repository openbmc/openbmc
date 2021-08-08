SUMMARY = "xxhash is a Python binding for the xxHash library by Yann Collet."
SECTION = "devel/python"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3a9dab7f159514231249aa3e108ab26b"

SRC_URI[sha256sum] = "b7bead8cf6210eadf9cecf356e17af794f57c0939a3d420a00d87ea652f87b49"

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
