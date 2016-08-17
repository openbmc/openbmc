SUMMARY = "Net::SSLeay - Perl extension for using OpenSSL"
DESCRIPTION = "This module offers some high level convenience functions for accessing \
web pages on SSL servers (for symmetry, same API is offered for \
accessing http servers, too), a sslcat() function for writing your own \
clients, and finally access to the SSL api of SSLeay/OpenSSL package \
so you can write servers or clients for more complicated applications."
HOMEPAGE = "http://search.cpan.org/dist/Net-SSLeay/"
SECTION = "libs"

LICENSE = "OpenSSL"
LIC_FILES_CHKSUM = "file://README;beginline=274;endline=294;md5=49f415984b387be999ee2ad0e5c692fe"

DEPENDS = "openssl zlib"
RDEPENDS_${PN} += "perl-module-carp \
                   perl-module-errno \
                   perl-module-extutils-makemaker \
                   perl-module-mime-base64 \
                   perl-module-socket \
                  "

SRC_URI = "http://search.cpan.org/CPAN/authors/id/M/MI/MIKEM/Net-SSLeay-${PV}.tar.gz \
           file://0001-libnet-ssleay-perl-Disable-test-that-fails-with-open.patch \
           file://0002-Recent-1.0.2-betas-have-dropped-the-SSLv3_method-fun.patch \
           file://run-ptest \
          "
SRC_URI[md5sum] = "19600c036e9e0bbfbf9157f083e40755"
SRC_URI[sha256sum] = "2fb1371120b85f018944d95736c107163f04ba56b6029c0709a2c3d6247b9c06"

S = "${WORKDIR}/Net-SSLeay-${PV}"

inherit cpan ptest

EXTRA_CPANFLAGS = "LIBS='-L=${STAGING_LIBDIR} -L=${STAGING_BASELIBDIR}' \
                   INC=-I=${STAGING_INCDIR} \
                   'EXTRALIBS=-lssl -lcrypto -lz' \
                   'LDLOADLIBS=-lssl -lcrypto -lz' \
                  "

do_configure_prepend() {
    export OPENSSL_PREFIX=${STAGING_DIR_NATIVE}${prefix_native}
}

do_install_ptest() {
    cp -r ${B}/t ${D}${PTEST_PATH}
}

BBCLASSEXTEND = "native"

FILES_${PN}-dbg =+ "${libdir}/perl/vendor_perl/*/auto/Net/SSLeay/.debug/"

RDEPENDS_${PN}-ptest = " perl"
