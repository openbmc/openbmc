DESCRIPTION = "XStatic base package with minimal support code"
HOMEPAGE = "https://pypi.python.org/pypi/XStatic"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://README.txt;md5=1418684272f85f400cebf1b1a255c5cd"

PYPI_PACKAGE = "XStatic"

SRC_URI[md5sum] = "dea172b7b14b0dbcd5ed63075221af4b"
SRC_URI[sha256sum] = "80b78dfe37bce6dee4343d64c65375a80bcf399b46dd47c0c7d56161568a23a8"

DEPENDS += " \
    ${PYTHON_PN}-pip \
"

inherit pypi setuptools3
