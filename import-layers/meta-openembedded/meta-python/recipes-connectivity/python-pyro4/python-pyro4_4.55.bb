SUMMARY = "Python Remote Objects"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=378acef375e17a3bff03bd0f78c53220"

SRC_URI[md5sum] = "0400a4d260dbeaa4e7e725c4ba310ead"
SRC_URI[sha256sum] = "49a7a142542d87dde1cecc8d3ee048ec9481ba861d61234d219fadd06e6ced96"

PYPI_PACKAGE = "Pyro4"

inherit pypi setuptools

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-logging \
    ${PYTHON_PN}-selectors34 \
    ${PYTHON_PN}-serpent \
    ${PYTHON_PN}-threading \
    ${PYTHON_PN}-zlib \
    "
