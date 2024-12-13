SUMMARY = "Simplifies building parse types based on the parse module"
HOMEPAGE = "https://github.com/jenisys/parse_type"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2e469278ace89c246d52505acc39c3da"

SRC_URI[sha256sum] = "8e99d2f52fab2f0f1f3d68ba9d026060140bf0e53680aada0111fb27b2f0e93a"
SRC_URI += " \
    file://run-ptest \
"

PYPI_PACKAGE = "parse_type"

inherit pypi ptest python_setuptools_build_meta

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}

RDEPENDS:${PN} += "python3-parse"
RDEPENDS:${PN}-ptest += " \
    python3-pytest \
    python3-six \
    python3-unittest-automake-output \
"
