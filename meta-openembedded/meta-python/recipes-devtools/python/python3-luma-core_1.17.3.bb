SUMMARY = "A component library to support SBC display drivers"
DESCRIPTION = "A component library to support SBC display drivers"
HOMEPAGE = "https://github.com/rm-hull/luma.core"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=db07e3d471938ea7d7fd2135f88ac9a1"

inherit pypi setuptools3

SRC_URI[md5sum] = "c13303a123c32c1a8dc5bc089a920822"
SRC_URI[sha256sum] = "1c928ad8e42af5fd395260176f89eb68a8af61911971f94124fabc9c6b550689"

CLEANBROKEN = "1"

PYPI_PACKAGE = "luma.core"

RDEPENDS_${PN} += " \
	${PYTHON_PN}-pillow \
	${PYTHON_PN}-threading \
	${PYTHON_PN}-smbus2 \
"
