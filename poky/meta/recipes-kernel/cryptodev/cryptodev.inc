HOMEPAGE = "http://cryptodev-linux.org/"
DESCRIPTION = "Cryptodev-linux is a device that allows access to Linux kernel \
cryptographic drivers; thus allowing of userspace applications to take advantage \
of hardware accelerators. Cryptodev-linux is implemented as a standalone \
module that requires no dependencies other than a stock linux kernel. Its \
API is compatible with OpenBSD's cryptodev userspace API (/dev/crypto)."

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "git://github.com/cryptodev-linux/cryptodev-linux \
           "
SRCREV = "a87053bee5680878c295b7d23cf0d7065576ac2b"

S = "${WORKDIR}/git"

CLEANBROKEN = "1"
