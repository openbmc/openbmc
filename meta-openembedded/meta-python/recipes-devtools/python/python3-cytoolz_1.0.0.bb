SUMMARY = "Cython implementation of the toolz package, which provides high \
performance utility functions for iterables, functions, and dictionaries."
HOMEPAGE = "https://github.com/pytoolz/cytoolz"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=d422ebce3e9c6447563bd410e9b22f2e"

SRC_URI[sha256sum] = "eb453b30182152f9917a5189b7d99046b6ce90cdf8aeb0feff4b2683e600defd"

inherit pypi setuptools3 cython

RDEPENDS:${PN} += "python3-toolz"
