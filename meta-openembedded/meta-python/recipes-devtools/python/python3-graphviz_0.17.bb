DESCRIPTION = "Graphviz protocol implementation"
HOMEPAGE = "https://graphviz.readthedocs.io/en/stable/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=87cd8818b7e63c6a9c580034e80d7521"

SRC_URI[sha256sum] = "ef6e2c5deb9cdcc0c7eece1d89625fd07b0f2208ea2bcb483520907ddf8b4e12"

inherit pypi setuptools3

PYPI_PACKAGE_EXT = "zip"

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-logging \
"

BBCLASSEXTEND = "native nativesdk"
