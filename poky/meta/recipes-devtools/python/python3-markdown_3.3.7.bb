SUMMARY = "A Python implementation of John Gruber's Markdown."
HOMEPAGE = "https://python-markdown.github.io/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=745aaad0c69c60039e638bff9ffc59ed"

inherit pypi python_setuptools_build_meta

PYPI_PACKAGE = "Markdown"
SRC_URI[sha256sum] = "cbb516f16218e643d8e0a95b309f77eb118cb138d39a4f27851e6a63581db874"

BBCLASSEXTEND = "native"

RDEPENDS:${PN} += "${PYTHON_PN}-logging ${PYTHON_PN}-setuptools"
