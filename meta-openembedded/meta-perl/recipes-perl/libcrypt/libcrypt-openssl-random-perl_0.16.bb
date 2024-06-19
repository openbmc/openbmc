SUMMARY = "Crypt::OpenSSL::Random - OpenSSL/LibreSSL pseudo-random number generator access"
SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0-or-later"

LIC_FILES_CHKSUM = "file://LICENSE;md5=089c18d635ae273e1727ec385e64063b"

SRC_URI = "http://www.cpan.org/modules/by-module/Crypt/Crypt-OpenSSL-Random-${PV}.tar.gz "
SRC_URI[sha256sum] = "fcf58cb2af4c3eda2fe1405527d9373efe9576268fce8adb34df9ce9b6b44d1e"

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
