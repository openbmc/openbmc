SUMMARY = "A Python implementation of John Gruber's Markdown."
HOMEPAGE = "https://python-markdown.github.io/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=745aaad0c69c60039e638bff9ffc59ed"

inherit pypi python_setuptools_build_meta

PYPI_PACKAGE = "Markdown"
SRC_URI[sha256sum] = "8bf101198e004dc93e84a12a7395e31aac6a9c9942848ae1d99b9d72cf9b3520"

BBCLASSEXTEND = "native nativesdk"

RDEPENDS:${PN} += "${PYTHON_PN}-logging ${PYTHON_PN}-setuptools"
