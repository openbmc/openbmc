DESCRIPTION = "Simple integration of Flask and WTForms."
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=d98d089889e14b227732d45dac3aacc4"

SRC_URI[sha256sum] = "41c4244e9ae626d63bed42ae4785b90667b885b1535d5a4095e1f63060d12aa9"

PYPI_PACKAGE = "Flask-WTF"

inherit pypi setuptools3

RDEPENDS:${PN} = "\
    ${PYTHON_PN}-flask \
    ${PYTHON_PN}-itsdangerous \
    ${PYTHON_PN}-json \
    ${PYTHON_PN}-wtforms \
"
