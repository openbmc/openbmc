DESCRIPTION = "Non-validating SQL parser module"
HOMEPAGE = "http://pypi.python.org/pypi/sqlparse"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2b136f573f5386001ea3b7b9016222fc"

SRC_URI += "file://0001-sqlparse-change-shebang-to-python3.patch \
            file://run-ptest \
	    "

SRC_URI[sha256sum] = "0c00730c74263a94e5a9919ade150dfc3b19c574389985446148402998287dae"

export BUILD_SYS
export HOST_SYS

inherit pypi ptest setuptools3

RDEPENDS:${PN}-ptest += " \
    ${PYTHON_PN}-pytest \
    ${PYTHON_PN}-unixadmin \
"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}

BBCLASSEXTEND = "native nativesdk"
