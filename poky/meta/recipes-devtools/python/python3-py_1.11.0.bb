SUMMARY = "Library with cross-python path, ini-parsing, io, code, log facilities"
HOMEPAGE = "http://py.readthedocs.io/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a6bb0320b04a0a503f12f69fea479de9"

SRC_URI[sha256sum] = "51c75c4126074b472f746a24399ad32f6053d1b34b68d2fa41e558e6f4a98719"

DEPENDS += "${PYTHON_PN}-setuptools-scm-native"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"

RDEPENDS:${PN} += "${PYTHON_PN}-netclient"
