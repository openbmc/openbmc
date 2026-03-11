SUMMARY =  "djangorestframework"
DESCRIPTION = "pip3 install djangorestframework"
HOMEPAGE = "https://pypi.python.org/pypi/djangorestframework"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=7879a5a716147a784f7e524c9cf103c1"

SRC_URI[sha256sum] = "f022ff46613584de994c0c6a4aebbace5fd700555fbe9d33b865ebf173eba6c9"

PYPI_PACKAGE = "djangorestframework"

inherit pypi setuptools3

RDEPENDS:${PN} += "\
    python3-django \
"
