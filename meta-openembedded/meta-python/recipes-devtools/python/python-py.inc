SUMMARY = "Library with cross-python path, ini-parsing, io, code, log facilities"
HOMEPAGE = "http://py.readthedocs.io/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a6bb0320b04a0a503f12f69fea479de9"

SRC_URI[md5sum] = "d9e30436ce7e79f30847f9b30b62e149"
SRC_URI[sha256sum] = "dc639b046a6e2cff5bbe40194ad65936d6ba360b52b3c3fe1d08a82dd50b5e53"

DEPENDS += "${PYTHON_PN}-setuptools-scm-native"
BBCLASSEXTEND = "native nativesdk"
