DESCRIPTION = "Jalali implementation of Python's datetime module"
HOMEPAGE = "https://github.com/slashmili/python-jalali"
LICENSE = "Python-2.0"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=f6890b2f685363312aff7f520831cdef"

SRC_URI[sha256sum] = "39d0be41076b3a3850c3bfa90817e7ed459edc0e9cadce37dc7229b11f121c7e"

PYPI_PACKAGE = "jdatetime"

inherit pypi setuptools3

CLEANBROKEN = "1"

RDEPENDS:${PN} += " \
        ${PYTHON_PN}-modules \
"

