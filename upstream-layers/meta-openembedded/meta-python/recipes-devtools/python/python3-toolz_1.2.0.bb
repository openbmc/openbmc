SUMMARY = "A set of utility functions for iterators, functions, and dictionaries."
HOMEPAGE = "https://github.com/pytoolz/toolz"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=ca09cab178326d18433aae982d1edf5d"

SRC_URI[sha256sum] = "9667a038e9d6ecba37995e26cb2f59ec6420b6ad8dd9677de59db9b956b08490"

inherit pypi python_setuptools_build_meta

DEPENDS += "python3-setuptools-git-versioning-native"

RDEPENDS:${PN} += " \
    python3-json \
    python3-math \
"
