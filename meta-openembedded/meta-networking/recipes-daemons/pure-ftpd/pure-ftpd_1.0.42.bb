SUMMARY = "FTP Server with a strong focus on software security"
DESCRIPTION = "Pure-FTPd is a free (BSD license), secure, production-quality and standard-conformant FTP server."
HOMEPAGE = "http://www.pureftpd.org/project/pure-ftpd"
SECTION = "net"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=90352fb2bfe17f4261687a0d6e09f489"

DEPENDS = "libcap virtual/crypt"


SRC_URI = "http://download.pureftpd.org/pub/pure-ftpd/releases/pure-ftpd-${PV}.tar.gz \
           file://0001-Remove-hardcoded-usr-local-includes-from-configure.a.patch \
           file://nostrip.patch \
"
SRC_URI[md5sum] = "4195af8f0e5ee2a798b1014071dae3a3"
SRC_URI[sha256sum] = "7be73a8e58b190a7054d2ae00c5e650cb9e091980420082d02ec3c3b68d8e7f9"

inherit autotools

EXTRA_OECONF = "--with-minimal"
PACKAGECONFIG[libsodium] ="ac_cv_lib_sodium_crypto_pwhash_scryptsalsa208sha256_str=yes, \
                       ac_cv_lib_sodium_crypto_pwhash_scryptsalsa208sha256_str=no, libsodium"
