DESCRIPTION = "A flexible forms validation and rendering library for python web development."
HOMEPAGE = "https://pypi.python.org/pypi/WTForms"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=c4660c132770d5d0a5757541f6b79493"

SRC_URI[md5sum] = "41c0008dbe7bd98892c58f7457a46a4a"
SRC_URI[sha256sum] = "0cdbac3e7f6878086c334aa25dc5a33869a3954e9d1e015130d65a69309b3b61"

PYPI_PACKAGE = "WTForms"

inherit pypi setuptools3

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-netserver \
    ${PYTHON_PN}-numbers \
    "
