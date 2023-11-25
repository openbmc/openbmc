SUMMARY = "Python package to parse, read and write Microsoft OLE2 files"
HOMEPAGE = "https://github.com/decalage2/olefile"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=e21a06208415d5eb2001555d37851362"

SRC_URI[sha256sum] = "133b031eaf8fd2c9399b78b8bc5b8fcbe4c31e85295749bb17a87cba8f3c3964"

inherit pypi setuptools3

PYPI_PACKAGE = "olefile"
PYPI_PACKAGE_EXT = "zip"

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-core \
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-logging \
"
