DESCRIPTION = "Simple integration of Flask and WTForms."
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3ca6bb31670492f791e6a9f2fb9f8a80"

SRC_URI[md5sum] = "1b60b7dd1245b0d0173c21123717af61"
SRC_URI[sha256sum] = "d417e3a0008b5ba583da1763e4db0f55a1269d9dd91dcc3eb3c026d3c5dbd720"

PYPI_PACKAGE = "Flask-WTF"

inherit pypi setuptools3

RDEPENDS_${PN} = "\
    ${PYTHON_PN}-flask \
    ${PYTHON_PN}-itsdangerous \
    ${PYTHON_PN}-json \
    ${PYTHON_PN}-wtforms \
"
