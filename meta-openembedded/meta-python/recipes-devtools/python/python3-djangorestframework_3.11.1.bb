SUMMARY =  "djangorestframework"
DESCRIPTION = "pip3 install djangorestframework"
HOMEPAGE = "https://pypi.python.org/pypi/djangorestframework"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=7879a5a716147a784f7e524c9cf103c1"

SRC_URI[md5sum] = "c8ea3e4c3c0d705acf70184faeb2b696"
SRC_URI[sha256sum] = "6dd02d5a4bd2516fb93f80360673bf540c3b6641fec8766b1da2870a5aa00b32"
PYPI_PACKAGE = "djangorestframework"

inherit pypi setuptools3

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-django \
"
