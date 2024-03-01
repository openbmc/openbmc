SUMMARY = "A component library to support SBC display drivers"
DESCRIPTION = "A component library to support SBC display drivers"
HOMEPAGE = "https://github.com/rm-hull/luma.core"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=2083293a38df91b8d470d3fe30069262"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "963c264164d4374f549d57db09599e0ca458cea1bd05e16939897619be4e6dbd"

PYPI_PACKAGE = "luma.core"

RDEPENDS:${PN} += " \
	python3-pillow \
	python3-threading \
	python3-smbus2 \
"
