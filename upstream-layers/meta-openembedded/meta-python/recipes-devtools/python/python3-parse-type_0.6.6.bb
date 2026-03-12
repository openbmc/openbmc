SUMMARY = "Simplifies building parse types based on the parse module"
HOMEPAGE = "https://github.com/jenisys/parse_type"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2e469278ace89c246d52505acc39c3da"

SRC_URI[sha256sum] = "513a3784104839770d690e04339a8b4d33439fcd5dd99f2e4580f9fc1097bfb2"
SRC_URI += " \
    file://run-ptest \
"

PYPI_PACKAGE = "parse_type"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

inherit pypi ptest python_setuptools_build_meta

DEPENDS += "python3-setuptools-scm-native"

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
