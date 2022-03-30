SUMMARY = "A component library to support SBC display drivers"
DESCRIPTION = "A component library to support SBC display drivers"
HOMEPAGE = "https://github.com/rm-hull/luma.core"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=3b1d500f5911ec7522f1f790d616e0ee"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "f293f5fff8946eea62af3a5d5d7da55c37d2b64aac6c9c90180a385da9f7d003"

PYPI_PACKAGE = "luma.core"

RDEPENDS:${PN} += " \
	${PYTHON_PN}-pillow \
	${PYTHON_PN}-threading \
	${PYTHON_PN}-smbus2 \
"
