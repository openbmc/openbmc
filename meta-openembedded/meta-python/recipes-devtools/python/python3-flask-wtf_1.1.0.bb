DESCRIPTION = "Simple integration of Flask and WTForms."
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=d98d089889e14b227732d45dac3aacc4"

SRC_URI[sha256sum] = "10fd267a41b6dee4f433ec8d6507d4cce4f1d71700280cf654a7cdbae6408d47"

PYPI_PACKAGE = "Flask-WTF"

inherit pypi setuptools3

RDEPENDS:${PN} = "\
    ${PYTHON_PN}-flask \
    ${PYTHON_PN}-itsdangerous \
    ${PYTHON_PN}-json \
    ${PYTHON_PN}-wtforms \
"
