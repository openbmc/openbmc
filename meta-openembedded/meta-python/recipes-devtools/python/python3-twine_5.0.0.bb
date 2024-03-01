DESCRIPTION = "Utilities for interacting with PyPI"
HOMEPAGE = "https://twine.readthedocs.io/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a3d1106b253a8d50dd82a4202a045b4c"

SRC_URI[sha256sum] = "89b0cc7d370a4b66421cc6102f269aa910fe0f1861c124f573cf2ddedbc10cf4"

inherit pypi python_setuptools_build_meta

DEPENDS += "\
	python3-setuptools-scm-native \
"

RDEPENDS:${PN} += " \
	python3-importlib-metadata \
"

BBCLASSEXTEND = "native"
