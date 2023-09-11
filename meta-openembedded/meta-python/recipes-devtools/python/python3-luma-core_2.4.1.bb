SUMMARY = "A component library to support SBC display drivers"
DESCRIPTION = "A component library to support SBC display drivers"
HOMEPAGE = "https://github.com/rm-hull/luma.core"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=b56ff1acb787606580264498947079fc"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "175663a4b0afde86ed5359f265fbb2ed978132cac4d9b42a30a3e0f0faf3f0d7"

PYPI_PACKAGE = "luma.core"

RDEPENDS:${PN} += " \
	${PYTHON_PN}-pillow \
	${PYTHON_PN}-threading \
	${PYTHON_PN}-smbus2 \
"
