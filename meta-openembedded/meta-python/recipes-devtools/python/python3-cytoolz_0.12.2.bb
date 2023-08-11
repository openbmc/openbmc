SUMMARY = "Cython implementation of the toolz package, which provides high \
performance utility functions for iterables, functions, and dictionaries."
HOMEPAGE = "https://github.com/pytoolz/cytoolz"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=d422ebce3e9c6447563bd410e9b22f2e"

SRC_URI[sha256sum] = "31d4b0455d72d914645f803d917daf4f314d115c70de0578d3820deb8b101f66"

inherit pypi setuptools3

DEPENDS += "${PYTHON_PN}-cython-native"
RDEPENDS:${PN} += "${PYTHON_PN}-toolz"
