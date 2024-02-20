SUMMARY = "A set of utility functions for iterators, functions, and dictionaries."
HOMEPAGE = "https://github.com/pytoolz/toolz"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=ca09cab178326d18433aae982d1edf5d"

SRC_URI[sha256sum] = "ecca342664893f177a13dac0e6b41cbd8ac25a358e5f215316d43e2100224f4d"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-json \
    python3-math \
"
