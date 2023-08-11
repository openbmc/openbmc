SUMMARY = "Python subprocess replacement"
HOMEPAGE = "https://github.com/amoffat/sh"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=5317094292296f03405f59ae5f6544b6"

SRC_URI[sha256sum] = "a18920f0839991bc9dfddb6dcc006c360b1064ba96257359f0ea356e9fa75a60"

PYPI_PACKAGE = "sh"

inherit pypi python_poetry_core

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-asyncio \
    ${PYTHON_PN}-codecs \
    ${PYTHON_PN}-compression \
    ${PYTHON_PN}-core \
    ${PYTHON_PN}-io \
    ${PYTHON_PN}-logging \
    ${PYTHON_PN}-resource \
    ${PYTHON_PN}-shell \
    ${PYTHON_PN}-terminal \
    ${PYTHON_PN}-threading \
    ${PYTHON_PN}-unixadmin \
    ${PYTHON_PN}-fcntl \
"
