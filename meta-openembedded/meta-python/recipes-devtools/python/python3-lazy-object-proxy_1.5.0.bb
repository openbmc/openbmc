SUMMARY = "A fast and thorough lazy object proxy"
HOMEPAGE = "https://python-lazy-object-proxy.readthedocs.io/"
LICENSE = "BSD-2-Clause"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9c5c2c74370826468065c5702b8a1fcf"

DEPENDS += "${PYTHON_PN}-setuptools-scm-native"

SRC_URI[md5sum] = "ea5c009f1995d86942d024714096624e"
SRC_URI[sha256sum] = "a0aed261060cd0372abf08d16399b1224dbb5b400312e6b00f2b23eabe1d4e96"

inherit pypi setuptools3
