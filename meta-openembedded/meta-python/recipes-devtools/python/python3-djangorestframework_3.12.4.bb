SUMMARY =  "djangorestframework"
DESCRIPTION = "pip3 install djangorestframework"
HOMEPAGE = "https://pypi.python.org/pypi/djangorestframework"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=7879a5a716147a784f7e524c9cf103c1"

SRC_URI[sha256sum] = "f747949a8ddac876e879190df194b925c177cdeb725a099db1460872f7c0a7f2"

PYPI_PACKAGE = "djangorestframework"

inherit pypi setuptools3

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-django \
"
