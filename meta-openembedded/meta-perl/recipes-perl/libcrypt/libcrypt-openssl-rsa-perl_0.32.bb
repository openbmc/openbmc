SUMMARY = "Crypt Openssl RSA cpan module"
SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a67ceecc5d9a91a5a0d003ba50c26346"

SRC_URI = "http://www.cpan.org/modules/by-module/Crypt/Crypt-OpenSSL-RSA-${PV}.tar.gz \
           file://0001-Fix-for-Issue-31.patch \
"

SRC_URI[sha256sum] = "adc74f0ae125c77f65d5dd32abb9c3429300a79543bf263494f333f9c0b62a61"

DEPENDS += "libcrypt-openssl-guess-perl-native openssl"

RDEPENDS:${PN} = " \
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

RDEPENDS:${PN}-ptest = " \
    ${PN} \
    perl-module-file-copy \
    perl-module-test \
    perl-module-test-more \
"
