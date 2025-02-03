DESCRIPTION = "Non-validating SQL parser module"
HOMEPAGE = "http://pypi.python.org/pypi/sqlparse"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2b136f573f5386001ea3b7b9016222fc"

SRC_URI[sha256sum] = "09f67787f56a0b16ecdbde1bfc7f5d9c3371ca683cfeaa8e6ff60b4807ec9272"

export BUILD_SYS
export HOST_SYS

inherit pypi ptest-python-pytest python_hatchling

RDEPENDS:${PN}-ptest += "\
    python3-mypy \
    python3-unixadmin \
"

BBCLASSEXTEND = "native nativesdk"
