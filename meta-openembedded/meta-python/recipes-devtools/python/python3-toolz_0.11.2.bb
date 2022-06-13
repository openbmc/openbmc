SUMMARY = "A set of utility functions for iterators, functions, and dictionaries."
HOMEPAGE = "https://github.com/pytoolz/toolz"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=ca09cab178326d18433aae982d1edf5d"

SRC_URI[sha256sum] = "6b312d5e15138552f1bda8a4e66c30e236c831b612b2bf0005f8a1df10a4bc33"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-json \
    python3-math \
"
