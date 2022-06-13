SUMMARY = "Cython implementation of the toolz package, which provides high \
performance utility functions for iterables, functions, and dictionaries."
HOMEPAGE = "https://github.com/pytoolz/cytoolz"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=efbcddfa5610ca0c07ecfa274a82b373"

SRC_URI[sha256sum] = "ea23663153806edddce7e4153d1d407d62357c05120a4e8485bddf1bd5ab22b4"

inherit pypi setuptools3

RDEPENDS:${PN} += "python3-toolz"
