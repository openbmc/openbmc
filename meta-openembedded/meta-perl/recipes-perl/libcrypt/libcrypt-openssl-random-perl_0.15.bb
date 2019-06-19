SUMMARY = "Crypt::OpenSSL::Random - OpenSSL/LibreSSL pseudo-random number generator access"
SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0+"

LIC_FILES_CHKSUM = "file://LICENSE;md5=089c18d635ae273e1727ec385e64063b"

SRC_URI = "http://www.cpan.org/modules/by-module/Crypt/Crypt-OpenSSL-Random-${PV}.tar.gz "
SRC_URI[md5sum] = "bcde8d5a822c91376deda3c4f0c75fbe"
SRC_URI[sha256sum] = "f0876faa1ba3111e39b86aa730c603211eff2905e460c72a57b61e8cf475cef4"

S = "${WORKDIR}/Crypt-OpenSSL-Random-${PV}"

DEPENDS += " \
    openssl \
    libcrypt-openssl-guess-perl-native \
"

EXTRA_CPANFLAGS = "INC='-I${STAGING_INCDIR}' LIBS='-L${STAGING_LIBDIR} -L${STAGING_BASELIBDIR} -lcrypto'"

inherit cpan ptest-perl

RDEPENDS_${PN} += "\
    perl-module-exporter \
    perl-module-strict \
    perl-module-vars \
    perl-module-xsloader \
"

RDEPENDS_${PN}-ptest += "\
    perl-module-file-copy \
    perl-module-test-more \
"

BBCLASSEXTEND = "native"
