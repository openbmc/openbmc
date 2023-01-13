DESCRIPTION = "Utilities for interacting with PyPI"
HOMEPAGE = "https://twine.readthedocs.io/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a3d1106b253a8d50dd82a4202a045b4c"

SRC_URI[sha256sum] = "9e102ef5fdd5a20661eb88fad46338806c3bd32cf1db729603fe3697b1bc83c8"

inherit pypi python_setuptools_build_meta

DEPENDS += "\
	${PYTHON_PN}-setuptools-scm-native \
"

RDEPENDS:${PN} += " \
	${PYTHON_PN}-importlib-metadata \
"

BBCLASSEXTEND = "native"
