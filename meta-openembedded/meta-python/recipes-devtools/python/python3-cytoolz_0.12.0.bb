SUMMARY = "Cython implementation of the toolz package, which provides high \
performance utility functions for iterables, functions, and dictionaries."
HOMEPAGE = "https://github.com/pytoolz/cytoolz"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=efbcddfa5610ca0c07ecfa274a82b373"

SRC_URI[sha256sum] = "c105b05f85e03fbcd60244375968e62e44fe798c15a3531c922d531018d22412"

inherit pypi setuptools3

DEPENDS += "${PYTHON_PN}-cython-native"
RDEPENDS:${PN} += "${PYTHON_PN}-toolz"
