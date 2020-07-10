SUMMARY = "Library with cross-python path, ini-parsing, io, code, log facilities"
HOMEPAGE = "http://py.readthedocs.io/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a6bb0320b04a0a503f12f69fea479de9"

SRC_URI[md5sum] = "b80db4e61eef724f49feb4d20b649e62"
SRC_URI[sha256sum] = "9ca6883ce56b4e8da7e79ac18787889fa5206c79dcc67fb065376cd2fe03f342"

DEPENDS += "${PYTHON_PN}-setuptools-scm-native"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"

RDEPENDS_${PN} += "${PYTHON_PN}-netclient"
