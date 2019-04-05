SUMMARY = "A gevent based messagpack rpc library"
DESCRIPTION = "mprpc is a fast implementation of the messagepack rpc protocol for python. \
It is based on gevent for handling connections and enabling concurrent connections."
HOMEPAGE = "https://github.com/studio-ousia/mprpc"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4ba825394aec026b5f94edca44426859"
DEPENDS += "${PYTHON_PN}-gevent"
RDEPENDS_${PN} += "${PYTHON_PN}-gevent ${PYTHON_PN}-msgpack ${PYTHON_PN}-gsocketpool"

SRC_URI[md5sum] = "8d97961051422f3de315613434982d3b"
SRC_URI[sha256sum] = "3589fd127482e291b1ec314d6f0e55cc13311c12932ace356d6178ea1ca28f6a"

inherit pypi
