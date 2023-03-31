SUMMARY = "Simple Python module for working with HTML/CSS color definitions."
HOMEPAGE = "https://pypi.org/project/webcolors/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=bcf737478d2fa8af5bc954231df056c6"

SRC_URI[sha256sum] = "16d043d3a08fd6a1b1b7e3e9e62640d09790dce80d2bdd4792a175b35fe794a9"

inherit pypi setuptools3 ptest

RDEPENDS:${PN}:class-target = "\
    ${PYTHON_PN}-stringold \
"

SRC_URI += " \
    file://run-ptest \
"

RDEPENDS:${PN}-ptest += " \
    ${PYTHON_PN}-pytest \
    ${PYTHON_PN}-unittest-automake-output \
"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}

BBCLASSEXTEND = "native nativesdk"
