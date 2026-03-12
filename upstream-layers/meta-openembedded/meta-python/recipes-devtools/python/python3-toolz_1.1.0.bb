SUMMARY = "A set of utility functions for iterators, functions, and dictionaries."
HOMEPAGE = "https://github.com/pytoolz/toolz"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=ca09cab178326d18433aae982d1edf5d"

SRC_URI[sha256sum] = "27a5c770d068c110d9ed9323f24f1543e83b2f300a687b7891c1a6d56b697b5b"

inherit pypi python_setuptools_build_meta

DEPENDS += "python3-setuptools-git-versioning-native"

RDEPENDS:${PN} += " \
    python3-json \
    python3-math \
"
