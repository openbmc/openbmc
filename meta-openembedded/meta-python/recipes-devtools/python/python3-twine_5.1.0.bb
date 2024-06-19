DESCRIPTION = "Utilities for interacting with PyPI"
HOMEPAGE = "https://twine.readthedocs.io/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a3d1106b253a8d50dd82a4202a045b4c"

SRC_URI[sha256sum] = "4d74770c88c4fcaf8134d2a6a9d863e40f08255ff7d8e2acb3cbbd57d25f6e9d"

inherit pypi python_setuptools_build_meta

DEPENDS += "\
	python3-setuptools-scm-native \
"

RDEPENDS:${PN} += " \
	python3-importlib-metadata \
"

BBCLASSEXTEND = "native"
