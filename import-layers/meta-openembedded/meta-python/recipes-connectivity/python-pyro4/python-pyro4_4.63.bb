SUMMARY = "Python Remote Objects"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=378acef375e17a3bff03bd0f78c53220"

SRC_URI[md5sum] = "e1d772b67bf7c6f75fa3174bc95c8839"
SRC_URI[sha256sum] = "67d2b34156619ba37e92100af95aade8129dd2b7327eb05821d43887451f7d7b"

PYPI_PACKAGE = "Pyro4"

inherit pypi setuptools

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-logging \
    ${PYTHON_PN}-selectors34 \
    ${PYTHON_PN}-serpent \
    ${PYTHON_PN}-threading \
    ${PYTHON_PN}-zlib \
    "
