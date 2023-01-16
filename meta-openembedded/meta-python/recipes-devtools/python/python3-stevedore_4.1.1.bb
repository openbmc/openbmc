DESCRIPTION = "Manage dynamic plugins for Python applications"
HOMEPAGE = "https://docs.openstack.org/stevedore/latest/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI[sha256sum] = "7f8aeb6e3f90f96832c301bff21a7eb5eefbe894c88c506483d355565d88cc1a"

DEPENDS += "${PYTHON_PN}-pbr-native"

inherit pypi setuptools3

RDEPENDS:${PN} += "${PYTHON_PN}-pbr ${PYTHON_PN}-six"

BBCLASSEXTEND = "native"
