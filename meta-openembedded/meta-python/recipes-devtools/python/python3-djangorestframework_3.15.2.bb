SUMMARY =  "djangorestframework"
DESCRIPTION = "pip3 install djangorestframework"
HOMEPAGE = "https://pypi.python.org/pypi/djangorestframework"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=7879a5a716147a784f7e524c9cf103c1"

SRC_URI[sha256sum] = "36fe88cd2d6c6bec23dca9804bab2ba5517a8bb9d8f47ebc68981b56840107ad"

PYPI_PACKAGE = "djangorestframework"

inherit pypi setuptools3

RDEPENDS:${PN} += "\
    python3-django \
"
