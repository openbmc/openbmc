SUMMARY = "A component library to support SBC display drivers"
DESCRIPTION = "A component library to support SBC display drivers"
HOMEPAGE = "https://github.com/rm-hull/luma.core"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=eda804060ba2312e41fe96b6fa334fd7"

inherit pypi setuptools3

SRC_URI[md5sum] = "9c890fa8e387a035ff0d1ae96a0715f1"
SRC_URI[sha256sum] = "e0e14c762695fb8758e8b15cfd28cd4c1618bf2fd93157c8770a2e8e0e254ae5"

CLEANBROKEN = "1"

PYPI_PACKAGE = "luma.core"

RDEPENDS_${PN} += " \
	${PYTHON_PN}-pillow \
	${PYTHON_PN}-threading \
	${PYTHON_PN}-smbus2 \
"
