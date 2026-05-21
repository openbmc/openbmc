SUMMARY = "FTP Server with a strong focus on software security"
DESCRIPTION = "Pure-FTPd is a free (BSD license), secure, production-quality and standard-conformant FTP server."
HOMEPAGE = "http://www.pureftpd.org/project/pure-ftpd"
SECTION = "net"
LICENSE = "0BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=384872cb5bfd3a52956dc3743ed9fd48"

DEPENDS = "libcap virtual/crypt"

SRC_URI = "http://download.pureftpd.org/pub/pure-ftpd/releases/pure-ftpd-${PV}.tar.gz \
           file://0001-Remove-hardcoded-usr-local-includes-from-configure.a.patch \
           file://nostrip.patch \
"
SRC_URI[sha256sum] = "dc9140420ec44f7829579591ff378aa6396b4604b9c6aeae847368e0f35bd7b2"

inherit autotools

EXTRA_AUTORECONF += "-I m4"

PACKAGECONFIG[libsodium] = "ac_cv_lib_sodium_crypto_pwhash_scryptsalsa208sha256_str=yes, \
                            ac_cv_lib_sodium_crypto_pwhash_scryptsalsa208sha256_str=no, libsodium"
