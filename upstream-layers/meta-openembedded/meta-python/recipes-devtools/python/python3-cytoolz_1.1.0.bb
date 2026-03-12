SUMMARY = "Cython implementation of the toolz package, which provides high \
performance utility functions for iterables, functions, and dictionaries."
HOMEPAGE = "https://github.com/pytoolz/cytoolz"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=d422ebce3e9c6447563bd410e9b22f2e"

SRC_URI[sha256sum] = "13a7bf254c3c0d28b12e2290b82aed0f0977a4c2a2bf84854fcdc7796a29f3b0"

inherit pypi python_setuptools_build_meta cython

DEPENDS += "python3-setuptools-git-versioning-native"

RDEPENDS:${PN} += "python3-toolz"
