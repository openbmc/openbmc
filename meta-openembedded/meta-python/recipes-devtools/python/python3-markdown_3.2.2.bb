SUMMARY = "A Python implementation of John Gruber's Markdown."
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=745aaad0c69c60039e638bff9ffc59ed"

inherit pypi setuptools3

PYPI_PACKAGE = "Markdown"
PYPI_SRC_URI = "https://files.pythonhosted.org/packages/44/30/cb4555416609a8f75525e34cbacfc721aa5b0044809968b2cf553fd879c7/Markdown-${PV}.tar.gz"
SRC_URI[md5sum] = "6e8daf1e566bf3572c137ada399fe40b"
SRC_URI[sha256sum] = "1fafe3f1ecabfb514a5285fca634a53c1b32a81cb0feb154264d55bf2ff22c17"

BBCLASSEXTEND = "native"

RDEPENDS_${PN} += "${PYTHON_PN}-logging ${PYTHON_PN}-setuptools"
