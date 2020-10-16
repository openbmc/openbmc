SUMMARY = "A Python implementation of John Gruber's Markdown."
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=745aaad0c69c60039e638bff9ffc59ed"

inherit pypi setuptools3

PYPI_PACKAGE = "Markdown"
SRC_URI[md5sum] = "76eb34a058bb8b637ccadc4ce384bae4"
SRC_URI[sha256sum] = "4f4172a4e989b97f96860fa434b89895069c576e2b537c4b4eed265266a7affc"

BBCLASSEXTEND = "native"

RDEPENDS_${PN} += "${PYTHON_PN}-logging ${PYTHON_PN}-setuptools"
