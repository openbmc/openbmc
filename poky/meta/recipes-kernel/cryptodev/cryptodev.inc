HOMEPAGE = "http://cryptodev-linux.org/"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "git://github.com/cryptodev-linux/cryptodev-linux \
           "
SRCREV = "e0c25e289d6baf1d83c2b9cb523d3bc237d0c0c9"

S = "${WORKDIR}/git"

CLEANBROKEN = "1"
