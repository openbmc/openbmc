SUMMARY = "A Python implementation of John Gruber's Markdown."
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=745aaad0c69c60039e638bff9ffc59ed"

inherit pypi setuptools3

PYPI_PACKAGE = "Markdown"
SRC_URI[md5sum] = "4bdebf1b5c64286c2214c99120da4292"
SRC_URI[sha256sum] = "4b71fbd2db30c1dfb400f22b25f41cb823fc1db0aa8b7b67d120644f92cc1011"

BBCLASSEXTEND = "native"

RDEPENDS_${PN} += "${PYTHON_PN}-logging ${PYTHON_PN}-setuptools"
