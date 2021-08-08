SUMMARY = "Python subprocess replacement"
HOMEPAGE = "https://github.com/amoffat/sh"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=5317094292296f03405f59ae5f6544b6"

SRC_URI[sha256sum] = "9d7bd0334d494b2a4609fe521b2107438cdb21c0e469ffeeb191489883d6fe0d"

PYPI_PACKAGE = "sh"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-codecs \
    ${PYTHON_PN}-core \
    ${PYTHON_PN}-io \
    ${PYTHON_PN}-logging \
    ${PYTHON_PN}-resource \
    ${PYTHON_PN}-shell \
    ${PYTHON_PN}-terminal \
    ${PYTHON_PN}-threading \
    ${PYTHON_PN}-unixadmin \
"
