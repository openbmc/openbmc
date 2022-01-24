SUMMARY = "A fast and thorough lazy object proxy"
HOMEPAGE = "https://python-lazy-object-proxy.readthedocs.io/"
LICENSE = "BSD-2-Clause"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9c5c2c74370826468065c5702b8a1fcf"

DEPENDS += "${PYTHON_PN}-setuptools-scm-native ${PYTHON_PN}-pip-native"

SRC_URI[sha256sum] = "d609c75b986def706743cdebe5e47553f4a5a1da9c5ff66d76013ef396b5a8a4"

inherit pypi setuptools3
