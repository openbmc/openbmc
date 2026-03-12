SUMMARY = "Net::SSLeay - Perl extension for using OpenSSL"
DESCRIPTION = "This module offers some high level convenience functions for accessing \
web pages on SSL servers (for symmetry, same API is offered for \
accessing http servers, too), a sslcat() function for writing your own \
clients, and finally access to the SSL api of SSLeay/OpenSSL package \
so you can write servers or clients for more complicated applications."
HOMEPAGE = "https://metacpan.org/dist/Net-SSLeay"
SECTION = "libs"

LICENSE = "Artistic-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c49f0a6dd21ce7d8988794dea20b650e"

DEPENDS = "openssl zlib openssl-native"
RDEPENDS:${PN} += "\
    libssl \
    libcrypto \
    perl-module-carp \
    perl-module-errno \
    perl-module-extutils-makemaker \
    perl-module-mime-base64 \
    perl-module-socket \
    perl-module-autoloader \
    zlib \
"

SRC_URI = "${CPAN_MIRROR}/authors/id/C/CH/CHRISN/Net-SSLeay-${PV}.tar.gz \
           file://0001-tests-Address-another-formatting-difference-in-OpenSSL-3.4.1.patch \
           file://0001-test-32_x509_get_cert_info-allow-single-colon.patch \
           file://run-ptest"
SRC_URI[sha256sum] = "9d7be8a56d1bedda05c425306cc504ba134307e0c09bda4a788c98744ebcd95d"

S = "${UNPACKDIR}/Net-SSLeay-${PV}"

inherit cpan ptest-perl

do_configure() {
    export OPENSSL_PREFIX="${STAGING_EXECPREFIXDIR}"
    cpan_do_configure
}

do_install_ptest_perl:append(){
    cp -r ${S}/inc ${D}${PTEST_PATH}
}

FILES:${PN}-dbg =+ "${libdir}/perl/vendor_perl/*/auto/Net/SSLeay/.debug/"

RDEPENDS:${PN}-ptest += "\
    perl-module-english \
    perl-module-file-spec-functions \
    perl-module-findbin \
    perl-module-perlio \
    perl-module-test-more \
    perl-module-threads"
