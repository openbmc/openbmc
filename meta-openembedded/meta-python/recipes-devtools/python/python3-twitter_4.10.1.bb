SUMMARY = "Twitter for Python"
DESCRIPTION = "Python module to support twitter API"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=9;endline=9;md5=8227180126797a0148f94f483f3e1489"

SRC_URI[sha256sum] = "310193775d7fc381abd6f37021a9af27f7e9edfcce5ec51bd73ea5f30c21fa61"

PYPI_PACKAGE = "tweepy"

inherit pypi setuptools3

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-pip \
    ${PYTHON_PN}-pysocks \
    ${PYTHON_PN}-requests \
    ${PYTHON_PN}-requests-oauthlib \
    ${PYTHON_PN}-six \
"
