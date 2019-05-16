DESCRIPTION = "Manage dynamic plugins for Python applications"
HOMEPAGE = "https://github.com/dreamhost/stevedore"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI[md5sum] = "02c747c39f4634465aa2590ceefddf59"
SRC_URI[sha256sum] = "7be098ff53d87f23d798a7ce7ae5c31f094f3deb92ba18059b1aeb1ca9fec0a0"

inherit pypi

DEPENDS += "${PYTHON_PN}-pbr-native"

RDEPENDS_${PN} += "${PYTHON_PN}-pbr ${PYTHON_PN}-six"

BBCLASSEXTEND = "native"
