SUMMARY = "A gevent based messagpack rpc library"
DESCRIPTION = "mprpc is a fast implementation of the messagepack rpc protocol for python. \
It is based on gevent for handling connections and enabling concurrent connections."
HOMEPAGE = "https://github.com/studio-ousia/mprpc"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4ba825394aec026b5f94edca44426859"
DEPENDS += "${PYTHON_PN}-gevent"
RDEPENDS_${PN} += "${PYTHON_PN}-gevent ${PYTHON_PN}-msgpack ${PYTHON_PN}-gsocketpool"

SRC_URI[md5sum] = "acde1f346411997d76ab89790a356823"
SRC_URI[sha256sum] = "e8ddd47e7a2fb2d3b203fcc522443ee5be56c8f7e2c6d915c047445951cef1fb"

inherit pypi
