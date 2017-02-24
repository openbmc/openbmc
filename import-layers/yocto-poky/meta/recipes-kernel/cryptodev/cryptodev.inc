HOMEPAGE = "http://cryptodev-linux.org/"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "http://download.gna.org/cryptodev-linux/cryptodev-linux-${PV}.tar.gz \
           file://06d6b560c6e45dc317dae47c74706fa43f4a31d8.patch \
           file://cb186f682679383e8b5806240927903730ce85d9.patch"

SRC_URI[md5sum] = "02644cc4cd02301e0b503a332eb2f0b5"
SRC_URI[sha256sum] = "67fabde9fb67b286a96c4f45b594b0eccd0f761b495705c18f2ae9461b831376"

S = "${WORKDIR}/cryptodev-linux-${PV}"

CLEANBROKEN = "1"
