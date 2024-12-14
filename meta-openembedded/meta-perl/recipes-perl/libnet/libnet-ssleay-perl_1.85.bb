SUMMARY = "Net::SSLeay - Perl extension for using OpenSSL"
DESCRIPTION = "This module offers some high level convenience functions for accessing \
web pages on SSL servers (for symmetry, same API is offered for \
accessing http servers, too), a sslcat() function for writing your own \
clients, and finally access to the SSL api of SSLeay/OpenSSL package \
so you can write servers or clients for more complicated applications."
HOMEPAGE = "https://metacpan.org/dist/Net-SSLeay"
SECTION = "libs"

LICENSE = "Artistic-1.0 | GPL-1.0-or-later"
LIC_FILES_CHKSUM = "file://README;beginline=274;endline=294;md5=67d67095d83e339da538a082fad5f38e"

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

SRC_URI = "${CPAN_MIRROR}/authors/id/M/MI/MIKEM/Net-SSLeay-${PV}.tar.gz \
           file://no-exec-on-configure.patch \
           file://run-ptest \
          "
SRC_URI[sha256sum] = "9d8188b9fb1cae3bd791979c20554925d5e94a138d00414f1a6814549927b0c8"

S = "${WORKDIR}/Net-SSLeay-${PV}"

inherit cpan ptest

do_configure() {
    export OPENSSL_PREFIX="${STAGING_EXECPREFIXDIR}"
    cpan_do_configure
}

do_install_ptest() {
    cp -r ${B}/t ${D}${PTEST_PATH}
}

FILES:${PN}-dbg =+ "${libdir}/perl/vendor_perl/*/auto/Net/SSLeay/.debug/"

RDEPENDS:${PN}-ptest = " perl"
