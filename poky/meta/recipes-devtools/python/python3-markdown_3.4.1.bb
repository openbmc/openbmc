SUMMARY = "A Python implementation of John Gruber's Markdown."
HOMEPAGE = "https://python-markdown.github.io/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=745aaad0c69c60039e638bff9ffc59ed"

inherit pypi python_setuptools_build_meta

PYPI_PACKAGE = "Markdown"
SRC_URI[sha256sum] = "3b809086bb6efad416156e00a0da66fe47618a5d6918dd688f53f40c8e4cfeff"

BBCLASSEXTEND = "native nativesdk"

RDEPENDS:${PN} += "${PYTHON_PN}-logging ${PYTHON_PN}-setuptools"
