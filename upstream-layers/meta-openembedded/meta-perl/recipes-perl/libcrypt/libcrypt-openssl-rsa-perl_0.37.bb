SUMMARY = "Crypt Openssl RSA cpan module"
SECTION = "libs"
HOMEPAGE = "https://metacpan.org/pod/Crypt::OpenSSL::RSA"
LICENSE = "Artistic-1.0 | GPL-1.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=399bd4de06d233aa49afa7c47cea8117"

SRC_URI = "${CPAN_MIRROR}/authors/id/T/TO/TODDR/Crypt-OpenSSL-RSA-${PV}.tar.gz \
"

SRC_URI[sha256sum] = "917f7312532f8f4af4f3acbf6ba10e0151f8577d2ef1f38e1035229be86eb6f4"

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
