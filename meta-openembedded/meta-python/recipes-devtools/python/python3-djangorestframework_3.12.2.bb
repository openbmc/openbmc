SUMMARY =  "djangorestframework"
DESCRIPTION = "pip3 install djangorestframework"
HOMEPAGE = "https://pypi.python.org/pypi/djangorestframework"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=7879a5a716147a784f7e524c9cf103c1"

SRC_URI[md5sum] = "e70d58ee2f83f11d4efe33162bb8af3b"
SRC_URI[sha256sum] = "0898182b4737a7b584a2c73735d89816343369f259fea932d90dc78e35d8ac33"
PYPI_PACKAGE = "djangorestframework"

inherit pypi setuptools3

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-django \
"
