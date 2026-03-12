DESCRIPTION = "Non-validating SQL parser module"
HOMEPAGE = "https://pypi.python.org/pypi/sqlparse"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2b136f573f5386001ea3b7b9016222fc"

SRC_URI[sha256sum] = "e20d4a9b0b8585fdf63b10d30066c7c94c5d7a7ec47c889a2d83a3caa93ff28e"

CVE_PRODUCT = "sqlparse"

export BUILD_SYS
export HOST_SYS

inherit pypi ptest-python-pytest python_hatchling

RDEPENDS:${PN}-ptest += "\
    python3-mypy \
    python3-unixadmin \
"

BBCLASSEXTEND = "native nativesdk"
