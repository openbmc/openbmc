SUMMARY = "A Python implementation of John Gruber's Markdown."
HOMEPAGE = "https://python-markdown.github.io/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=745aaad0c69c60039e638bff9ffc59ed"

inherit pypi python_setuptools_build_meta

PYPI_PACKAGE = "Markdown"
SRC_URI[sha256sum] = "a807eb2e4778d9156c8f07876c6e4d50b5494c5665c4834f67b06459dfd877b3"

BBCLASSEXTEND = "native nativesdk"

RDEPENDS:${PN} += "${PYTHON_PN}-logging ${PYTHON_PN}-setuptools"
