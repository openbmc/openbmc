SUMMARY =  "djangorestframework"
DESCRIPTION = "pip3 install djangorestframework"
HOMEPAGE = "https://pypi.python.org/pypi/djangorestframework"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=7879a5a716147a784f7e524c9cf103c1"

SRC_URI[sha256sum] = "579a333e6256b09489cbe0a067e66abe55c6595d8926be6b99423786334350c8"

PYPI_PACKAGE = "djangorestframework"

inherit pypi setuptools3

RDEPENDS:${PN} += "\
    python3-django \
"
