DESCRIPTION = "Non-validating SQL parser module"
HOMEPAGE = "http://pypi.python.org/pypi/sqlparse"
SECTION = "devel/python"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2b136f573f5386001ea3b7b9016222fc"

SRC_URI += "file://0001-sqlparse-change-shebang-to-python3.patch \
            file://run-ptest \
	    "

SRC_URI[md5sum] = "423047887a3590b04dd18f8caf843a2f"
SRC_URI[sha256sum] = "e162203737712307dfe78860cc56c8da8a852ab2ee33750e33aeadf38d12c548"

export BUILD_SYS
export HOST_SYS

inherit pypi ptest setuptools3

RDEPENDS_${PN}-ptest += " \
    ${PYTHON_PN}-pytest \
    ${PYTHON_PN}-unixadmin \
"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}

BBCLASSEXTEND = "native nativesdk"
