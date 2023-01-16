SUMMARY = "Twitter for Python"
DESCRIPTION = "Python module to support twitter API"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=9;endline=9;md5=8227180126797a0148f94f483f3e1489"

SRC_URI[sha256sum] = "5e4c5b5d22f9e5dd9678a708fae4e40e6eeb1a860a89891a5de3040d5f3da8fe"

PYPI_PACKAGE = "tweepy"

inherit pypi setuptools3

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-pip \
    ${PYTHON_PN}-pysocks \
    ${PYTHON_PN}-requests \
    ${PYTHON_PN}-requests-oauthlib \
    ${PYTHON_PN}-six \
"
