SUMMARY = "Library with cross-python path, ini-parsing, io, code, log facilities"
HOMEPAGE = "http://py.readthedocs.io/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a6bb0320b04a0a503f12f69fea479de9"

SRC_URI[md5sum] = "42c67de84b07ac9cc867b8b70843a45b"
SRC_URI[sha256sum] = "5e27081401262157467ad6e7f851b7aa402c5852dbcb3dae06768434de5752aa"

DEPENDS += "${PYTHON_PN}-setuptools-scm-native"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"

RDEPENDS_${PN} += "${PYTHON_PN}-netclient"
