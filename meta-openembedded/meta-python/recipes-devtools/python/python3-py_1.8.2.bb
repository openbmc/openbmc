SUMMARY = "Library with cross-python path, ini-parsing, io, code, log facilities"
HOMEPAGE = "http://py.readthedocs.io/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a6bb0320b04a0a503f12f69fea479de9"

SRC_URI[md5sum] = "baa54e458875d68031c422abb2e47a5c"
SRC_URI[sha256sum] = "f3b3a4c36512a4c4f024041ab51866f11761cc169670204b235f6b20523d4e6b"

DEPENDS += "${PYTHON_PN}-setuptools-scm-native"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"

RDEPENDS_${PN} += "${PYTHON_PN}-netclient"
