DESCRIPTION = "Graphviz protocol implementation"
HOMEPAGE = "https://graphviz.readthedocs.io/en/stable/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=87cd8818b7e63c6a9c580034e80d7521"

SRC_URI[sha256sum] = "09ed0cde452d015fe77c4845a210eb642f28d245f5bc250d4b97808cb8f49078"

inherit pypi setuptools3

PYPI_PACKAGE_EXT = "zip"

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-logging \
"

BBCLASSEXTEND = "native nativesdk"
