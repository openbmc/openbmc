SUMMARY = "Cython implementation of the toolz package, which provides high \
performance utility functions for iterables, functions, and dictionaries."
HOMEPAGE = "https://github.com/pytoolz/cytoolz"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=d422ebce3e9c6447563bd410e9b22f2e"

SRC_URI[sha256sum] = "89cc3161b89e1bb3ed7636f74ed2e55984fd35516904fc878cae216e42b2c7d6"

inherit pypi setuptools3 cython

RDEPENDS:${PN} += "python3-toolz"
