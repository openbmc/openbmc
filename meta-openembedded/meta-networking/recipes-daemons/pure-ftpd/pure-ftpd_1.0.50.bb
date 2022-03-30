SUMMARY = "FTP Server with a strong focus on software security"
DESCRIPTION = "Pure-FTPd is a free (BSD license), secure, production-quality and standard-conformant FTP server."
HOMEPAGE = "http://www.pureftpd.org/project/pure-ftpd"
SECTION = "net"
LICENSE = "0BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=a4496a14dea009df36c612707d455d02"

DEPENDS = "libcap virtual/crypt"

SRC_URI = "http://download.pureftpd.org/pub/pure-ftpd/releases/pure-ftpd-${PV}.tar.gz \
           file://0001-Remove-hardcoded-usr-local-includes-from-configure.a.patch \
           file://nostrip.patch \
"
SRC_URI[sha256sum] = "abe2f94eb40b330d4dc22b159991f44e5e515212f8e887049dccdef266d0ea23"

inherit autotools

PACKAGECONFIG[libsodium] ="ac_cv_lib_sodium_crypto_pwhash_scryptsalsa208sha256_str=yes, \
                           ac_cv_lib_sodium_crypto_pwhash_scryptsalsa208sha256_str=no, libsodium"
