DESCRIPTION = "Non-validating SQL parser module"
HOMEPAGE = "http://pypi.python.org/pypi/sqlparse"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2b136f573f5386001ea3b7b9016222fc"

SRC_URI:append = " \
    file://run-ptest \
"

SRC_URI[sha256sum] = "bb6b4df465655ef332548e24f08e205afc81b9ab86cb1c45657a7ff173a3a00e"

export BUILD_SYS
export HOST_SYS

inherit pypi ptest python_hatchling

RDEPENDS:${PN}-ptest += "\
    python3-mypy \
    python3-pytest \
    python3-unittest-automake-output \
    python3-unixadmin \
"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}

BBCLASSEXTEND = "native nativesdk"
