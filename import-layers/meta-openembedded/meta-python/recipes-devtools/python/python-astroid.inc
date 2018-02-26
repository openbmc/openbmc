SUMMARY = "An abstract syntax tree for Python with inference support."
HOMEPAGE = "https://pypi.python.org/pypi/astroid"
SECTION = "devel/python"
LICENSE = "LGPL-2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

SRC_URI[md5sum] = "6f65e4ea8290ec032320460905afb828"
SRC_URI[sha256sum] = "492c2a2044adbf6a84a671b7522e9295ad2f6a7c781b899014308db25312dd35"

inherit pypi

RDEPENDS_${PN}_class-target += "\
    ${PYTHON_PN}-distutils \
    ${PYTHON_PN}-lazy-object-proxy \
    ${PYTHON_PN}-six \
    ${PYTHON_PN}-subprocess \
    ${PYTHON_PN}-wrapt \
    ${PYTHON_PN}-re \
    ${PYTHON_PN}-setuptools \
"
