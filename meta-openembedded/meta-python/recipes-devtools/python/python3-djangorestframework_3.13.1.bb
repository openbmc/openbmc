SUMMARY =  "djangorestframework"
DESCRIPTION = "pip3 install djangorestframework"
HOMEPAGE = "https://pypi.python.org/pypi/djangorestframework"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=7879a5a716147a784f7e524c9cf103c1"

SRC_URI[sha256sum] = "0c33407ce23acc68eca2a6e46424b008c9c02eceb8cf18581921d0092bc1f2ee"

PYPI_PACKAGE = "djangorestframework"

inherit pypi setuptools3

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-django \
"
