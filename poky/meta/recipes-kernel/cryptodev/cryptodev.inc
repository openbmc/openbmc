HOMEPAGE = "http://cryptodev-linux.org/"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "git://github.com/cryptodev-linux/cryptodev-linux \
           "
SRCREV = "a87053bee5680878c295b7d23cf0d7065576ac2b"

S = "${WORKDIR}/git"

CLEANBROKEN = "1"
