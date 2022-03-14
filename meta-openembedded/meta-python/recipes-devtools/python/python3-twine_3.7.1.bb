DESCRIPTION = "Utilities for interacting with PyPI"
HOMEPAGE = "https://twine.readthedocs.io/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a3d1106b253a8d50dd82a4202a045b4c"

SRC_URI[sha256sum] = "28460a3db6b4532bde6a5db6755cf2dce6c5020bada8a641bb2c5c7a9b1f35b8"

inherit pypi setuptools3

DEPENDS += "\
	${PYTHON_PN}-setuptools-scm-native \
"

RDEPENDS:${PN} += " \
	${PYTHON_PN}-importlib-metadata \
"

do_compile:prepend() {
	echo "from setuptools import setup" > ${S}/setup.py
	echo "setup()" >> ${S}/setup.py
}

BBCLASSEXTEND = "native"
