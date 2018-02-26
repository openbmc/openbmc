DESCRIPTION = "Python package for creating and manipulating graphs and networks"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=925586ea588eb990de840dc71ea3752f"

SRC_URI[md5sum] = "6ef584a879e9163013e9a762e1cf7cd1"
SRC_URI[sha256sum] = "0d0e70e10dfb47601cbb3425a00e03e2a2e97477be6f80638fef91d54dd1e4b8"

inherit pypi setuptools

RDEPENDS_${PN} += "python-2to3"
