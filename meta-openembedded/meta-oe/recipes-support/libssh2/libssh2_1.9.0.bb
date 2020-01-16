SUMMARY = "A client-side C library implementing the SSH2 protocol"
HOMEPAGE = "http://www.libssh2.org/"
SECTION = "libs"

DEPENDS = "zlib"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=c5cf34fc0acb44b082ef50ef5e4354ca"

SRC_URI = "http://www.libssh2.org/download/${BP}.tar.gz \
           file://CVE-2019-17498.patch \
"
SRC_URI[md5sum] = "1beefafe8963982adc84b408b2959927"
SRC_URI[sha256sum] = "d5fb8bd563305fd1074dda90bd053fb2d29fc4bce048d182f96eaa466dfadafd"

inherit autotools pkgconfig

EXTRA_OECONF += "\
                 --with-libz \
                 --with-libz-prefix=${STAGING_LIBDIR} \
                "

# only one of openssl and gcrypt could be set
PACKAGECONFIG ??= "openssl"
PACKAGECONFIG[openssl] = "--with-crypto=openssl --with-libssl-prefix=${STAGING_LIBDIR}, , openssl"
PACKAGECONFIG[gcrypt] = "--with-crypto=libgcrypt --with-libgcrypt-prefix=${STAGING_EXECPREFIXDIR}, , libgcrypt"

BBCLASSEXTEND = "native nativesdk"
