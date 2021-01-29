SUMMARY = "A fast and thorough lazy object proxy"
HOMEPAGE = "https://python-lazy-object-proxy.readthedocs.io/"
LICENSE = "BSD-2-Clause"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9c5c2c74370826468065c5702b8a1fcf"

DEPENDS += "${PYTHON_PN}-setuptools-scm-native"

SRC_URI[sha256sum] = "5944a9b95e97de1980c65f03b79b356f30a43de48682b8bdd90aa5089f0ec1f4"

inherit pypi setuptools3
