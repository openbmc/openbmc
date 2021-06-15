SUMMARY = "Crypt Openssl RSA cpan module"
SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a67ceecc5d9a91a5a0d003ba50c26346"

SRC_URI = "http://www.cpan.org/modules/by-module/Crypt/Crypt-OpenSSL-RSA-${PV}.tar.gz "

SRC_URI[md5sum] = "d33681e19d2094df7c26bc7a4509265e"
SRC_URI[sha256sum] = "4173403ad4cf76732192099f833fbfbf3cd8104e0246b3844187ae384d2c5436"

DEPENDS += "libcrypt-openssl-guess-perl-native openssl"

RDEPENDS_${PN} = " \
    libcrypt-openssl-random-perl \
    perl-module-autoloader \
    perl-module-carp \
    perl-module-strict \
    perl-module-warnings \
    perl-module-xsloader \
"

EXTRA_CPANFLAGS = "INC='-I${STAGING_INCDIR}' LIBS='-L${STAGING_LIBDIR} -lssl -L${STAGING_DIR_TARGET}${base_libdir} -lcrypto'"

S = "${WORKDIR}/Crypt-OpenSSL-RSA-${PV}"

inherit cpan ptest-perl

do_compile() {
    export OTHERLDFLAGS='-Wl,-rpath'
    cpan_do_compile
}

RDEPENDS_${PN}-ptest = " \
    ${PN} \
    perl-module-file-copy \
    perl-module-test \
    perl-module-test-more \
"
