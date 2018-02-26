SUMMARY = "Python Remote Objects"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=378acef375e17a3bff03bd0f78c53220"

SRC_URI[md5sum] = "6e2fef3dc8b29773a87abbaa8e9abe15"
SRC_URI[sha256sum] = "e301edfb2bc47768b7222a68cae8de8be796d1d9f61cdbd1af9039985ed5009c"

PYPI_PACKAGE = "Pyro4"

inherit pypi setuptools

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-logging \
    ${PYTHON_PN}-selectors34 \
    ${PYTHON_PN}-serpent \
    ${PYTHON_PN}-threading \
    ${PYTHON_PN}-zlib \
    "
