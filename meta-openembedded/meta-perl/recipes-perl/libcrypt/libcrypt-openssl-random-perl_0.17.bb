SUMMARY = "Crypt::OpenSSL::Random - OpenSSL/LibreSSL pseudo-random number generator access"
SECTION = "libs"
HOMEPAGE = "https://metacpan.org/pod/Crypt::OpenSSL::Random"
LICENSE = "Artistic-1.0 | GPL-1.0-or-later"

LIC_FILES_CHKSUM = "file://LICENSE;md5=9e876d4149406d88b4ff1b37645363ad"

SRC_URI = "${CPAN_MIRROR}/authors/id/R/RU/RURBAN/Crypt-OpenSSL-Random-${PV}.tar.gz"
SRC_URI[sha256sum] = "a571b24181baaa76c96704e92acffc6934ff593e380dade274db4e43c140ad51"

S = "${WORKDIR}/Crypt-OpenSSL-Random-${PV}"

DEPENDS += " \
    openssl \
    libcrypt-openssl-guess-perl-native \
"

EXTRA_CPANFLAGS = "INC='-I${STAGING_INCDIR}' LIBS='-L${STAGING_LIBDIR} -L${STAGING_BASELIBDIR} -lcrypto'"

inherit cpan ptest-perl

RDEPENDS:${PN} += "\
    perl-module-exporter \
    perl-module-strict \
    perl-module-vars \
    perl-module-xsloader \
"

RDEPENDS:${PN}-ptest += "\
    perl-module-file-copy \
    perl-module-test-more \
"

BBCLASSEXTEND = "native"
