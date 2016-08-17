SUMMARY = "Crypt Openssl RSA cpan module"
SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = "file://LICENSE;md5=385c55653886acac3821999a3ccd17b3"

SRC_URI = "http://www.cpan.org/modules/by-module/Crypt/Crypt-OpenSSL-RSA-${PV}.tar.gz "

SRC_URI[md5sum] = "86217a5036fc63779c30420b5fd84129"
SRC_URI[sha256sum] = "5357f977464bb3a8184cf2d3341851a10d5515b4b2b0dfb88bf78995c0ded7be"

DEPENDS += "openssl"

RDEPENDS_${PN}="libcrypt-openssl-random-perl"

EXTRA_CPANFLAGS = "INC='-I${STAGING_INCDIR}' LIBS='-L${STAGING_LIBDIR} -lssl -L${STAGING_DIR_TARGET}${base_libdir} -lcrypto'"

S = "${WORKDIR}/Crypt-OpenSSL-RSA-${PV}"

inherit cpan

do_compile() {
    export OTHERLDFLAGS='-Wl,-rpath'
    cpan_do_compile
}
