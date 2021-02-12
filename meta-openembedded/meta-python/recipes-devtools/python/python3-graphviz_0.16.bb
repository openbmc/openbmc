DESCRIPTION = "Graphviz protocol implementation"
HOMEPAGE = "https://graphviz.readthedocs.io/en/stable/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=f7831c564aeff14e68aa4ff7a93dc89f"

SRC_URI[sha256sum] = "d2d25af1c199cad567ce4806f0449cb74eb30cf451fd7597251e1da099ac6e57"

inherit pypi setuptools3

PYPI_PACKAGE_EXT = "zip"

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-logging \
"

BBCLASSEXTEND = "native nativesdk"
