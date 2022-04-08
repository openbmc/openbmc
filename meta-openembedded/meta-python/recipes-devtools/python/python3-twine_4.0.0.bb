DESCRIPTION = "Utilities for interacting with PyPI"
HOMEPAGE = "https://twine.readthedocs.io/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a3d1106b253a8d50dd82a4202a045b4c"

SRC_URI[sha256sum] = "817aa0c0bdc02a5ebe32051e168e23c71a0608334e624c793011f120dbbc05b7"

inherit pypi python_setuptools_build_meta

DEPENDS += "\
	${PYTHON_PN}-setuptools-scm-native \
"

RDEPENDS:${PN} += " \
	${PYTHON_PN}-importlib-metadata \
"

BBCLASSEXTEND = "native"
