SUMMARY = "Cython implementation of the toolz package, which provides high \
performance utility functions for iterables, functions, and dictionaries."
HOMEPAGE = "https://github.com/pytoolz/cytoolz"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=d422ebce3e9c6447563bd410e9b22f2e"

SRC_URI[sha256sum] = "fdd8ded8a93e1be009577fccddaacf78aa21cbe7dc6ec53c229def0198a1ffa5"

inherit pypi python_setuptools_build_meta cython

DEPENDS += "python3-setuptools-git-versioning-native"

RDEPENDS:${PN} += "python3-toolz"
