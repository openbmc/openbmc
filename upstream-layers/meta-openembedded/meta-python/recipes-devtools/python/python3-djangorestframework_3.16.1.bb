SUMMARY =  "djangorestframework"
DESCRIPTION = "pip3 install djangorestframework"
HOMEPAGE = "https://pypi.python.org/pypi/djangorestframework"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=7879a5a716147a784f7e524c9cf103c1"

SRC_URI[sha256sum] = "166809528b1aced0a17dc66c24492af18049f2c9420dbd0be29422029cfc3ff7"

PYPI_PACKAGE = "djangorestframework"

inherit pypi setuptools3

RDEPENDS:${PN} += "\
    python3-django \
"
