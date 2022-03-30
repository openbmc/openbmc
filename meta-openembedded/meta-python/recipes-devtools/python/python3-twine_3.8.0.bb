DESCRIPTION = "Utilities for interacting with PyPI"
HOMEPAGE = "https://twine.readthedocs.io/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a3d1106b253a8d50dd82a4202a045b4c"

SRC_URI[sha256sum] = "8efa52658e0ae770686a13b675569328f1fba9837e5de1867bfe5f46a9aefe19"

inherit pypi python_setuptools_build_meta

DEPENDS += "\
	${PYTHON_PN}-setuptools-scm-native \
"

RDEPENDS:${PN} += " \
	${PYTHON_PN}-importlib-metadata \
"

BBCLASSEXTEND = "native"
