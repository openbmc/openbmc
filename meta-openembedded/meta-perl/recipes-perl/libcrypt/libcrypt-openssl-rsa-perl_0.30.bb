SUMMARY = "Crypt Openssl RSA cpan module"
SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a67ceecc5d9a91a5a0d003ba50c26346"

SRC_URI = "http://www.cpan.org/modules/by-module/Crypt/Crypt-OpenSSL-RSA-${PV}.tar.gz "

SRC_URI[md5sum] = "10bca2fc6d0ba1aa329f227424ae87d5"
SRC_URI[sha256sum] = "23e13531397af102db4fd24bcf70137add7c85c23cca697c43aa71c2959a29ac"

DEPENDS += "libcrypt-openssl-guess-native openssl"

RDEPENDS_${PN}="libcrypt-openssl-random-perl"

EXTRA_CPANFLAGS = "INC='-I${STAGING_INCDIR}' LIBS='-L${STAGING_LIBDIR} -lssl -L${STAGING_DIR_TARGET}${base_libdir} -lcrypto'"

S = "${WORKDIR}/Crypt-OpenSSL-RSA-${PV}"

inherit cpan

do_compile() {
    export OTHERLDFLAGS='-Wl,-rpath'
    cpan_do_compile
}
