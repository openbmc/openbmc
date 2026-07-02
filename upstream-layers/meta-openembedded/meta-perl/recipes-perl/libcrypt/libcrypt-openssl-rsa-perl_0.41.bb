SUMMARY = "Crypt Openssl RSA cpan module"
SECTION = "libs"
HOMEPAGE = "https://metacpan.org/pod/Crypt::OpenSSL::RSA"
LICENSE = "Artistic-1.0 | GPL-1.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=399bd4de06d233aa49afa7c47cea8117"

SRC_URI = "${CPAN_MIRROR}/authors/id/T/TI/TIMLEGGE/Crypt-OpenSSL-RSA-${PV}.tar.gz \
"

SRC_URI[sha256sum] = "82fa839897b88e9c245b6265f3be26d3bc879cae4c7a8151fad4a307c3366822"

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

S = "${UNPACKDIR}/Crypt-OpenSSL-RSA-${PV}"

inherit cpan ptest-perl

do_compile() {
    export OTHERLDFLAGS='-Wl,-rpath'
    cpan_do_compile
}

RDEPENDS:${PN}-ptest += " \
    perl-module-file-copy \
    perl-module-test \
    perl-module-test-more \
    perl-module-lib \
    libcrypt-openssl-guess-perl \
    perl-module-english \
"
