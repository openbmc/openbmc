DESCRIPTION = "Manage dynamic plugins for Python applications"
HOMEPAGE = "https://github.com/dreamhost/stevedore"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI[md5sum] = "525186cba577852e0408c92c3f8fac65"
SRC_URI[sha256sum] = "1e153545aca7a6a49d8337acca4f41c212fbfa60bf864ecd056df0cafb9627e8"

inherit pypi

DEPENDS += "${PYTHON_PN}-pbr-native"

RDEPENDS_${PN} += "${PYTHON_PN}-pbr ${PYTHON_PN}-six"

BBCLASSEXTEND = "native"
