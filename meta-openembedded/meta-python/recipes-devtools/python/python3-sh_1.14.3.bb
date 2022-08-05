SUMMARY = "Python subprocess replacement"
HOMEPAGE = "https://github.com/amoffat/sh"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=5317094292296f03405f59ae5f6544b6"

SRC_URI[sha256sum] = "e4045b6c732d9ce75d571c79f5ac2234edd9ae4f5fa9d59b09705082bdca18c7"

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
    ${PYTHON_PN}-fcntl \
"
