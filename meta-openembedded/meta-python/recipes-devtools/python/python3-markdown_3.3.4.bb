SUMMARY = "A Python implementation of John Gruber's Markdown."
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=745aaad0c69c60039e638bff9ffc59ed"

inherit pypi setuptools3

PYPI_PACKAGE = "Markdown"
SRC_URI[sha256sum] = "31b5b491868dcc87d6c24b7e3d19a0d730d59d3e46f4eea6430a321bed387a49"

BBCLASSEXTEND = "native"

RDEPENDS_${PN} += "${PYTHON_PN}-logging ${PYTHON_PN}-setuptools"
