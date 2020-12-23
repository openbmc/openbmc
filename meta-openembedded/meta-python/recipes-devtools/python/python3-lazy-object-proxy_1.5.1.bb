SUMMARY = "A fast and thorough lazy object proxy"
HOMEPAGE = "https://python-lazy-object-proxy.readthedocs.io/"
LICENSE = "BSD-2-Clause"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9c5c2c74370826468065c5702b8a1fcf"

DEPENDS += "${PYTHON_PN}-setuptools-scm-native"

SRC_URI[md5sum] = "c168cec73acdec25a49f6f467f5b1eaa"
SRC_URI[sha256sum] = "9723364577b79ad9958a68851fe2acb94da6fd25170c595516a8289e6a129043"

inherit pypi setuptools3
