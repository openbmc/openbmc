SUMMARY = "A set of utility functions for iterators, functions, and dictionaries."
HOMEPAGE = "https://github.com/pytoolz/toolz"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=ca09cab178326d18433aae982d1edf5d"

SRC_URI[sha256sum] = "88c570861c440ee3f2f6037c4654613228ff40c93a6c25e0eba70d17282c6194"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-json \
    python3-math \
"
