DESCRIPTION = "Python HTTP for Humans."
HOMEPAGE = "http://python-requests.org"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=58c7e163c9f8ee037246da101c6afd1e"

SRC_URI[md5sum] = "0b7f480d19012ec52bab78292efd976d"
SRC_URI[sha256sum] = "c577815dd00f1394203fc44eb979724b098f88264a9ef898ee45b8e5e9cf587f"

RDEPENDS_${PN} = "${PYTHON_PN}-email ${PYTHON_PN}-json ${PYTHON_PN}-netserver"
