SUMMARY =  "djangorestframework"
DESCRIPTION = "pip3 install djangorestframework"
HOMEPAGE = "https://pypi.python.org/pypi/djangorestframework"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=7879a5a716147a784f7e524c9cf103c1"

SRC_URI[sha256sum] = "a6def5f447fe78ff853bff1d47a3c59bf38f5434b031780b351b0c73a62db1a5"

PYPI_PACKAGE = "djangorestframework"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += "\
    python3-django \
"
