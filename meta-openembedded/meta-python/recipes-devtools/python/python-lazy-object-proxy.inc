SUMMARY = "A fast and thorough lazy object proxy"
HOMEPAGE = "https://python-lazy-object-proxy.readthedocs.io/"
LICENSE = "BSD-2-Clause"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9c5c2c74370826468065c5702b8a1fcf"

DEPENDS += "${PYTHON_PN}-setuptools-scm-native"

SRC_URI[md5sum] = "5c64c06affcd2a7c6ddc848af4280cca"
SRC_URI[sha256sum] = "f3900e8a5de27447acbf900b4750b0ddfd7ec1ea7fbaf11dfa911141bc522af0"

inherit pypi
