SUMMARY = "FTP Server with a strong focus on software security"
DESCRIPTION = "Pure-FTPd is a free (BSD license), secure, production-quality and standard-conformant FTP server."
HOMEPAGE = "http://www.pureftpd.org/project/pure-ftpd"
SECTION = "net"
LICENSE = "0BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=194bc994ad6bbd4ff5a021082fe52156"

DEPENDS = "libcap virtual/crypt"

SRC_URI = "http://download.pureftpd.org/pub/pure-ftpd/releases/pure-ftpd-${PV}.tar.gz \
           file://0001-Remove-hardcoded-usr-local-includes-from-configure.a.patch \
           file://nostrip.patch \
"
SRC_URI[sha256sum] = "4160f66b76615eea2397eac4ea3f0a146b7928207b79bc4cc2f99ad7b7bd9513"

inherit autotools

PACKAGECONFIG[libsodium] ="ac_cv_lib_sodium_crypto_pwhash_scryptsalsa208sha256_str=yes, \
                           ac_cv_lib_sodium_crypto_pwhash_scryptsalsa208sha256_str=no, libsodium"
