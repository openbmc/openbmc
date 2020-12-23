HOMEPAGE = "http://cryptodev-linux.org/"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "git://github.com/cryptodev-linux/cryptodev-linux \
           "
SRCREV = "fabe5989a3dc9fba0cd0a40b612247cdde351c01"

S = "${WORKDIR}/git"

CLEANBROKEN = "1"
