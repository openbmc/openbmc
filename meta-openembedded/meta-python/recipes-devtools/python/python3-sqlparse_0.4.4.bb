DESCRIPTION = "Non-validating SQL parser module"
HOMEPAGE = "http://pypi.python.org/pypi/sqlparse"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2b136f573f5386001ea3b7b9016222fc"

SRC_URI += "file://0001-sqlparse-change-shebang-to-python3.patch \
            file://run-ptest \
	    "

SRC_URI[sha256sum] = "d446183e84b8349fa3061f0fe7f06ca94ba65b426946ffebe6e3e8295332420c"

export BUILD_SYS
export HOST_SYS

inherit pypi ptest python_flit_core

RDEPENDS:${PN}-ptest += " \
    python3-pytest \
    python3-unittest-automake-output \
    python3-unixadmin \
"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}

BBCLASSEXTEND = "native nativesdk"
