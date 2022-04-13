SUMMARY = "Twitter for Python"
DESCRIPTION = "Python module to support twitter API"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=8227180126797a0148f94f483f3e1489"

SRC_URI[sha256sum] = "8ba5774ac1663b09e5fce1b030daf076f2c9b3ddbf2e7e7ea0bae762e3b1fe3e"

PYPI_PACKAGE = "tweepy"

inherit pypi setuptools3

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-pip \
    ${PYTHON_PN}-pysocks \
    ${PYTHON_PN}-requests \
    ${PYTHON_PN}-six \
"
