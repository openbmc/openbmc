SUMMARY =  "djangorestframework"
DESCRIPTION = "pip3 install djangorestframework"
HOMEPAGE = "https://pypi.python.org/pypi/djangorestframework"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=7879a5a716147a784f7e524c9cf103c1"

SRC_URI[md5sum] = "47d5ea46923a131c5fbefeb610c6ce2c"
SRC_URI[sha256sum] = "d54452aedebb4b650254ca092f9f4f5df947cb1de6ab245d817b08b4f4156249"
PYPI_PACKAGE = "djangorestframework"

inherit pypi setuptools3

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-django \
"
