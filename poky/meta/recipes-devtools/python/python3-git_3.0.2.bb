SUMMARY = "Python library used to interact with Git repositories"
DESCRIPTION = "GitPython provides object model read and write access to \
a git repository. Access repository information conveniently, alter the \
index directly, handle remotes, or go down to low-level object database \
access with big-files support."
HOMEPAGE = "http://github.com/gitpython-developers/GitPython"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8b8d26c37c1d5a04f9b0186edbebc183"

PYPI_PACKAGE = "GitPython"

inherit pypi setuptools3

SRC_URI[md5sum] = "ccec14a5c9f18702406122983d80032c"
SRC_URI[sha256sum] = "d2f4945f8260f6981d724f5957bc076398ada55cb5d25aaee10108bcdc894100"

DEPENDS += " ${PYTHON_PN}-gitdb"

RDEPENDS_${PN} += " \
                   ${PYTHON_PN}-datetime \
                   ${PYTHON_PN}-gitdb \
                   ${PYTHON_PN}-io \
                   ${PYTHON_PN}-logging \
                   ${PYTHON_PN}-math \
                   ${PYTHON_PN}-netclient \
                   ${PYTHON_PN}-stringold \
                   ${PYTHON_PN}-unittest \
                   ${PYTHON_PN}-unixadmin \
                   git \
"

BBCLASSEXTEND = "native nativesdk"
