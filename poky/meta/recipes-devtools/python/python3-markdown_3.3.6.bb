SUMMARY = "A Python implementation of John Gruber's Markdown."
HOMEPAGE = "https://python-markdown.github.io/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=745aaad0c69c60039e638bff9ffc59ed"

inherit pypi python_setuptools_build_meta

PYPI_PACKAGE = "Markdown"
SRC_URI[sha256sum] = "76df8ae32294ec39dcf89340382882dfa12975f87f45c3ed1ecdb1e8cefc7006"

BBCLASSEXTEND = "native"

RDEPENDS:${PN} += "${PYTHON_PN}-logging ${PYTHON_PN}-setuptools"
