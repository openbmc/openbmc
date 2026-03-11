SUMMARY = "A set of utility functions for iterators, functions, and dictionaries."
HOMEPAGE = "https://github.com/pytoolz/toolz"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=ca09cab178326d18433aae982d1edf5d"

SRC_URI[sha256sum] = "2c86e3d9a04798ac556793bced838816296a2f085017664e4995cb40a1047a02"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-json \
    python3-math \
"
