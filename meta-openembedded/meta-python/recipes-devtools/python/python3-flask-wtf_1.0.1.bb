DESCRIPTION = "Simple integration of Flask and WTForms."
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=d98d089889e14b227732d45dac3aacc4"

SRC_URI[sha256sum] = "34fe5c6fee0f69b50e30f81a3b7ea16aa1492a771fe9ad0974d164610c09a6c9"

PYPI_PACKAGE = "Flask-WTF"

inherit pypi setuptools3

RDEPENDS:${PN} = "\
    ${PYTHON_PN}-flask \
    ${PYTHON_PN}-itsdangerous \
    ${PYTHON_PN}-json \
    ${PYTHON_PN}-wtforms \
"
