DESCRIPTION = "Graphviz protocol implementation"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI[sha256sum] = "e104ba036c8aef84320ec80560e544cd3cad68c9f90394b4e2b87bc44ab09791"

inherit pypi setuptools3

PYPI_PACKAGE_EXT = "zip"

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-logging \
"

BBCLASSEXTEND = "native nativesdk"
