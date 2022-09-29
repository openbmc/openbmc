DESCRIPTION = "Utilities for interacting with PyPI"
HOMEPAGE = "https://twine.readthedocs.io/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a3d1106b253a8d50dd82a4202a045b4c"

SRC_URI[sha256sum] = "96b1cf12f7ae611a4a40b6ae8e9570215daff0611828f5fe1f37a16255ab24a0"

inherit pypi python_setuptools_build_meta

DEPENDS += "\
	${PYTHON_PN}-setuptools-scm-native \
"

RDEPENDS:${PN} += " \
	${PYTHON_PN}-importlib-metadata \
"

BBCLASSEXTEND = "native"
