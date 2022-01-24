SUMMARY = "Twitter for Python"
DESCRIPTION = "Python module to support twitter API"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=8227180126797a0148f94f483f3e1489"

SRC_URI[sha256sum] = "8d4b4520271b796fa7efc4c5d5ef3228af4d79f6a4d3ace3900b2778ed8f6f1c"

PYPI_PACKAGE = "tweepy"

inherit pypi setuptools3

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-pip \
    ${PYTHON_PN}-pysocks \
    ${PYTHON_PN}-requests \
    ${PYTHON_PN}-six \
"
