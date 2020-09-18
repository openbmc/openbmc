SUMMARY = "Twitter for Python"
DESCRIPTION = "Python module to support twitter API"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=8227180126797a0148f94f483f3e1489"

SRC_URI[md5sum] = "b5bc640fa9f6baff6471c127aba1fec0"
SRC_URI[sha256sum] = "bfd19a5c11f35f7f199c795f99d9cbf8a52eb33f0ecfb6c91ee10b601180f604"

PYPI_PACKAGE = "tweepy"

inherit pypi setuptools3

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-pip \
    ${PYTHON_PN}-pysocks \
    ${PYTHON_PN}-requests \
    ${PYTHON_PN}-six \
"
