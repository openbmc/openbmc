HOMEPAGE = "http://cryptodev-linux.org/"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "http://download.gna.org/cryptodev-linux/cryptodev-linux-${PV}.tar.gz"

SRC_URI[md5sum] = "0b63b3481cf2c90386b35f057481d36b"
SRC_URI[sha256sum] = "41880533b53de4d7b3f054e230f576988dafb8eed7bef5ebcf6422bb2e3a3b25"

S = "${WORKDIR}/cryptodev-linux-${PV}"

CLEANBROKEN = "1"
