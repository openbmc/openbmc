DESCRIPTION = "Manage dynamic plugins for Python applications"
HOMEPAGE = "https://docs.openstack.org/stevedore/latest/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI[sha256sum] = "59b58edb7f57b11897f150475e7bc0c39c5381f0b8e3fa9f5c20ce6c89ec4aa1"

DEPENDS += "${PYTHON_PN}-pbr-native"

inherit pypi setuptools3

RDEPENDS:${PN} += "${PYTHON_PN}-pbr ${PYTHON_PN}-six"

BBCLASSEXTEND = "native"
