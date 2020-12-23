SUMMARY = "xxhash is a Python binding for the xxHash library by Yann Collet."
SECTION = "devel/python"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5a8d76283514a1b7e6a414aba38629b5"

SRC_URI[md5sum] = "010fda0427b621e5fe6930ad42511d88"
SRC_URI[sha256sum] = "58ca818554c1476fa1456f6cd4b87002e2294f09baf0f81e5a2a4968e62c423c"

SRC_URI += " \
    file://run-ptest \
"

inherit pypi setuptools3 ptest

RDEPENDS_${PN}-ptest += " \
    ${PYTHON_PN}-pytest \
"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}
