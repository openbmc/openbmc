SUMMARY = "Cython implementation of the toolz package, which provides high \
performance utility functions for iterables, functions, and dictionaries."
HOMEPAGE = "https://github.com/pytoolz/cytoolz"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=d422ebce3e9c6447563bd410e9b22f2e"

SRC_URI[sha256sum] = "fc33909397481c90de3cec831bfb88d97e220dc91939d996920202f184b4648e"

inherit pypi setuptools3

DEPENDS += "${PYTHON_PN}-cython-native"
RDEPENDS:${PN} += "${PYTHON_PN}-toolz"
