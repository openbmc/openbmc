SUMMARY = "A gevent based messagpack rpc library"
DESCRIPTION = "mprpc is a fast implementation of the messagepack rpc protocol for python. \
It is based on gevent for handling connections and enabling concurrent connections."
HOMEPAGE = "https://github.com/studio-ousia/mprpc"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4ba825394aec026b5f94edca44426859"
DEPENDS += "${PYTHON_PN}-gevent"
RDEPENDS_${PN} += "${PYTHON_PN}-gevent ${PYTHON_PN}-msgpack ${PYTHON_PN}-gsocketpool"

SRC_URI[md5sum] = "66e6cf367ea71cfed8938a27e9c183c3"
SRC_URI[sha256sum] = "5ccada6526d48ac4d95a7bbdc093586663841560337032a503f57ccfae69d7bf"

inherit pypi
