DESCRIPTION = "Python package for creating and manipulating graphs and networks"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=3376ff7c9c58048c62d91431f7f08cde"

SRC_URI[md5sum] = "4a2c2a62dfc46ba7d594daca7c834995"
SRC_URI[sha256sum] = "64272ca418972b70a196cb15d9c85a5a6041f09a2f32e0d30c0255f25d458bb1"

PYPI_PACKAGE_EXT = "zip"

inherit pypi setuptools

RDEPENDS_${PN} += "python-2to3"
