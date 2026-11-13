SUMMARY = "Crypt::OpenSSL::Bignum - OpenSSL's multiprecision integer arithmetic"
DESCRIPTION = "Crypt::OpenSSL::Bignum provides access to OpenSSL multiprecision \
integer arithmetic libraries. It is used by other OpenSSL modules, such as \
Crypt::OpenSSL::RSA, to provide bignum values like key parameters."
HOMEPAGE = "https://metacpan.org/dist/Crypt-OpenSSL-Bignum"
SECTION = "libs"
LICENSE = "Artistic-1.0 OR GPL-1.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=385c55653886acac3821999a3ccd17b3"

SRC_URI = "${CPAN_MIRROR}/authors/id/K/KM/KMX/Crypt-OpenSSL-Bignum-${PV}.tar.gz"
SRC_URI[sha256sum] = "234e72fb8396d45527e6fd45e43759c5c3f3a208cf8f29e6a22161a996fd42dc"

S = "${UNPACKDIR}/Crypt-OpenSSL-Bignum-${PV}"

DEPENDS += "openssl"

EXTRA_CPANFLAGS = "INC='-I${STAGING_INCDIR}' LIBS='-L${STAGING_LIBDIR} -L${STAGING_BASELIBDIR} -lcrypto'"

inherit cpan

RDEPENDS:${PN} += "\
    perl-module-carp \
    perl-module-dynaloader \
"

BBCLASSEXTEND = "native"
