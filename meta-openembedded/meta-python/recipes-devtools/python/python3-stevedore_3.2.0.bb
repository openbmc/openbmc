DESCRIPTION = "Manage dynamic plugins for Python applications"
HOMEPAGE = "https://docs.openstack.org/stevedore/latest/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI[md5sum] = "6a39764d7aecc63a0990cf4635cb430d"
SRC_URI[sha256sum] = "38791aa5bed922b0a844513c5f9ed37774b68edc609e5ab8ab8d8fe0ce4315e5"

DEPENDS += "${PYTHON_PN}-pbr-native"

inherit pypi setuptools3

RDEPENDS_${PN} += "${PYTHON_PN}-pbr ${PYTHON_PN}-six"

BBCLASSEXTEND = "native"
