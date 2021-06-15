SUMMARY = "FTP Server with a strong focus on software security"
DESCRIPTION = "Pure-FTPd is a free (BSD license), secure, production-quality and standard-conformant FTP server."
HOMEPAGE = "http://www.pureftpd.org/project/pure-ftpd"
SECTION = "net"
LICENSE = "BSD-0-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=e25e28bc568d70eb26c3a91387c86ccb"

DEPENDS = "libcap virtual/crypt"

SRC_URI = "http://download.pureftpd.org/pub/pure-ftpd/releases/pure-ftpd-${PV}.tar.gz \
           file://0001-Remove-hardcoded-usr-local-includes-from-configure.a.patch \
           file://nostrip.patch \
"
SRC_URI[md5sum] = "451879495ba61c1d7dcfca8dd231119f"
SRC_URI[sha256sum] = "767bf458c70b24f80c0bb7a1bbc89823399e75a0a7da141d30051a2b8cc892a5"

inherit autotools

PACKAGECONFIG[libsodium] ="ac_cv_lib_sodium_crypto_pwhash_scryptsalsa208sha256_str=yes, \
                           ac_cv_lib_sodium_crypto_pwhash_scryptsalsa208sha256_str=no, libsodium"
