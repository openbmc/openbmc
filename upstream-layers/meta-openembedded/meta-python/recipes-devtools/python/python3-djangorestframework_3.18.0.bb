SUMMARY =  "djangorestframework"
DESCRIPTION = "pip3 install djangorestframework"
HOMEPAGE = "https://pypi.python.org/pypi/djangorestframework"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=7879a5a716147a784f7e524c9cf103c1"

SRC_URI[sha256sum] = "2323a5111837e0b784dcb8323abc78ecc54fa2a5af7aff2677cf50cdd849477f"


inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += "\
    python3-django \
"
