DESCRIPTION = "Non-validating SQL parser module"
HOMEPAGE = "http://pypi.python.org/pypi/sqlparse"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2b136f573f5386001ea3b7b9016222fc"

SRC_URI:append = " \
    file://run-ptest \
"

SRC_URI[sha256sum] = "9e37b35e16d1cc652a2545f0997c1deb23ea28fa1f3eefe609eee3063c3b105f"

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
