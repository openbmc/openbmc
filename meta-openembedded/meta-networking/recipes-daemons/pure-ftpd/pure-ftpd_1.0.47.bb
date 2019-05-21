SUMMARY = "FTP Server with a strong focus on software security"
DESCRIPTION = "Pure-FTPd is a free (BSD license), secure, production-quality and standard-conformant FTP server."
HOMEPAGE = "http://www.pureftpd.org/project/pure-ftpd"
SECTION = "net"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=0595b4261a04bc2d27f30b9c90796c1f"

DEPENDS = "libcap virtual/crypt"

SRC_URI = "http://download.pureftpd.org/pub/pure-ftpd/releases/pure-ftpd-${PV}.tar.gz \
           file://0001-Remove-hardcoded-usr-local-includes-from-configure.a.patch \
           file://nostrip.patch \
"
SRC_URI[md5sum] = "f000e519918682ee6b65090352177d4a"
SRC_URI[sha256sum] = "4740c316f5df879a2d68464489fb9b8b90113fe7dce58e2cdd2054a4768f27ad"

inherit autotools

EXTRA_OECONF = "--with-minimal"
PACKAGECONFIG[libsodium] ="ac_cv_lib_sodium_crypto_pwhash_scryptsalsa208sha256_str=yes, \
                           ac_cv_lib_sodium_crypto_pwhash_scryptsalsa208sha256_str=no, libsodium"
