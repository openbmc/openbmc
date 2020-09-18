SUMMARY = "Python subprocess replacement"
HOMEPAGE = "https://github.com/amoffat/sh"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=5317094292296f03405f59ae5f6544b6"

SRC_URI[md5sum] = "4cbbcb85c081d78f4b0f00d634db9bb9"
SRC_URI[sha256sum] = "05c7e520cdf70f70a7228a03b589da9f96c6e0d06fc487ab21fc62b26a592e59"

PYPI_PACKAGE = "sh"

inherit pypi setuptools3

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-codecs \
    ${PYTHON_PN}-core \
    ${PYTHON_PN}-io \
    ${PYTHON_PN}-logging \
    ${PYTHON_PN}-resource \
    ${PYTHON_PN}-shell \
    ${PYTHON_PN}-terminal \
    ${PYTHON_PN}-tests \
    ${PYTHON_PN}-threading \
    ${PYTHON_PN}-unixadmin \
"
