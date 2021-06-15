SUMMARY = "Library with cross-python path, ini-parsing, io, code, log facilities"
HOMEPAGE = "http://py.readthedocs.io/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a6bb0320b04a0a503f12f69fea479de9"

SRC_URI[sha256sum] = "21b81bda15b66ef5e1a777a21c4dcd9c20ad3efd0b3f817e7a809035269e1bd3"

DEPENDS += "${PYTHON_PN}-setuptools-scm-native"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"

RDEPENDS_${PN} += "${PYTHON_PN}-netclient"
