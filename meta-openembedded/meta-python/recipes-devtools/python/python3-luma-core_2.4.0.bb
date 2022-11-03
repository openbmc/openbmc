SUMMARY = "A component library to support SBC display drivers"
DESCRIPTION = "A component library to support SBC display drivers"
HOMEPAGE = "https://github.com/rm-hull/luma.core"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=71cded473ab60fdbe20edc519217f521"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "cf5fdf3563d5ec56e2f792f3a2f432abaeac517a0b05a10a757a4c5a26bb2e5d"

PYPI_PACKAGE = "luma.core"

RDEPENDS:${PN} += " \
	${PYTHON_PN}-pillow \
	${PYTHON_PN}-threading \
	${PYTHON_PN}-smbus2 \
"
