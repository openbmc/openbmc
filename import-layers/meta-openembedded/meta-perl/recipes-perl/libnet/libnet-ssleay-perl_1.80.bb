SUMMARY = "Net::SSLeay - Perl extension for using OpenSSL"
DESCRIPTION = "This module offers some high level convenience functions for accessing \
web pages on SSL servers (for symmetry, same API is offered for \
accessing http servers, too), a sslcat() function for writing your own \
clients, and finally access to the SSL api of SSLeay/OpenSSL package \
so you can write servers or clients for more complicated applications."
HOMEPAGE = "http://search.cpan.org/dist/Net-SSLeay/"
SECTION = "libs"

LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = "file://README;beginline=274;endline=294;md5=67d67095d83e339da538a082fad5f38e"

DEPENDS = "openssl zlib openssl-native"
RDEPENDS_${PN} += "perl-module-carp \
                   perl-module-errno \
                   perl-module-extutils-makemaker \
                   perl-module-mime-base64 \
                   perl-module-socket \
                  "

SRC_URI = "http://search.cpan.org/CPAN/authors/id/M/MI/MIKEM/Net-SSLeay-${PV}.tar.gz \
           file://run-ptest \
          "
SRC_URI[md5sum] = "b1e0fd96d7f3be57580377226ca5f089"
SRC_URI[sha256sum] = "a541a66d59fc5f9bb7af09d3a0b68305e4c43ba9488b12915728fd282ea01b93"

S = "${WORKDIR}/Net-SSLeay-${PV}"

inherit cpan ptest

EXTRA_CPANFLAGS = "LIBS='-L=${STAGING_LIBDIR} -L=${STAGING_BASELIBDIR}' \
                   INC=-I=${STAGING_INCDIR} \
                   '-lssl -lcrypto -lz' \
                  "

do_install_ptest() {
    cp -r ${B}/t ${D}${PTEST_PATH}
}

BBCLASSEXTEND = "native"

FILES_${PN}-dbg =+ "${libdir}/perl/vendor_perl/*/auto/Net/SSLeay/.debug/"

RDEPENDS_${PN}-ptest = " perl"
