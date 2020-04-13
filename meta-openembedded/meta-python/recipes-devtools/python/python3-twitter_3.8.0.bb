SUMMARY = "Twitter for Python"
DESCRIPTION = "Python module to support twitter API"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=8f702b489acb6863cec8b261a55931d8"

SRC_URI[md5sum] = "8aeff278b7cefcd384c65929bc921e2c"
SRC_URI[sha256sum] = "8abd828ba51a85a2b5bb7373715d6d3bb32d18ac624e3a4db02e4ef8ab48316b"

PYPI_PACKAGE = "tweepy"

inherit pypi setuptools3

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-pip \
    ${PYTHON_PN}-pysocks \
    ${PYTHON_PN}-requests \
    ${PYTHON_PN}-six \
"
