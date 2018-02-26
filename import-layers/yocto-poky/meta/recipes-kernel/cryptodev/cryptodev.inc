HOMEPAGE = "http://cryptodev-linux.org/"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "http://nwl.cc/pub/cryptodev-linux/cryptodev-linux-${PV}.tar.gz"

SRC_URI[md5sum] = "cb4e0ed9e5937716c7c8a7be84895b6d"
SRC_URI[sha256sum] = "9f4c0b49b30e267d776f79455d09c70cc9c12c86eee400a0d0a0cd1d8e467950"

S = "${WORKDIR}/cryptodev-linux-${PV}"

CLEANBROKEN = "1"
