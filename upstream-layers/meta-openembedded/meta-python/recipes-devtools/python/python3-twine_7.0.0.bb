DESCRIPTION = "Utilities for interacting with PyPI"
HOMEPAGE = "https://twine.readthedocs.io/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a3d1106b253a8d50dd82a4202a045b4c"

SRC_URI[sha256sum] = "85cdb29c518efef867360ae4acd4b0dfd61c8654a22fca08e6f8539f05022177"

inherit pypi python_setuptools_build_meta

DEPENDS += "\
	python3-setuptools-scm-native \
"

RDEPENDS:${PN} += " \
	python3-importlib-metadata \
"

BBCLASSEXTEND = "native"
