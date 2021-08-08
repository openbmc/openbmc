DESCRIPTION = "Simple integration of Flask and WTForms."
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3ca6bb31670492f791e6a9f2fb9f8a80"

SRC_URI[sha256sum] = "ff177185f891302dc253437fe63081e7a46a4e99aca61dfe086fb23e54fff2dc"

PYPI_PACKAGE = "Flask-WTF"

inherit pypi setuptools3

RDEPENDS:${PN} = "\
    ${PYTHON_PN}-flask \
    ${PYTHON_PN}-itsdangerous \
    ${PYTHON_PN}-json \
    ${PYTHON_PN}-wtforms \
"
