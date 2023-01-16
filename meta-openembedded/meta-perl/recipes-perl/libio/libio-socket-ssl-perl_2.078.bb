SUMMARY = "Perl library for transparent SSL"
DESCRIPTION = "This module is a true drop-in replacement for IO::Socket::INET that \
uses SSL to encrypt data before it is transferred to a remote server \
or client. IO::Socket::SSL supports all the extra features that one \
needs to write a full-featured SSL client or server application: \
multiple SSL contexts, cipher selection, certificate verification, and \
SSL version selection. As an extra bonus, it works perfectly with \
mod_perl."
HOMEPAGE = "http://search.cpan.org/dist/IO-Socket-SSL/"
SECTION = "libs"

LICENSE = "Artistic-1.0 | GPL-1.0-or-later"
LIC_FILES_CHKSUM = "file://META.yml;beginline=12;endline=12;md5=963ce28228347875ace682de56eef8e8"

RDEPENDS:${PN} += "\
    libmozilla-ca-perl \
    libnet-ssleay-perl \
    perl-module-autoloader \
    perl-module-io-socket \
    perl-module-scalar-util \
"

SRC_URI = "${CPAN_MIRROR}/authors/id/S/SU/SULLR/IO-Socket-SSL-${PV}.tar.gz"
SRC_URI[sha256sum] = "4cf83737a72b0970948b494bc9ddab7f725420a0ca0152d25c7e48ef8fa2b6a1"

S = "${WORKDIR}/IO-Socket-SSL-${PV}"

inherit cpan ptest-perl

do_install:append () {
    mkdir -p ${D}${docdir}/${PN}/
    cp ${S}/BUGS ${D}${docdir}/${PN}/
    cp ${S}/Changes ${D}${docdir}/${PN}/
    cp ${S}/README ${D}${docdir}/${PN}/
    cp -pRP ${S}/docs ${D}${docdir}/${PN}/
    cp -pRP ${S}/certs ${D}${docdir}/${PN}/
    cp -pRP ${S}/example ${D}${docdir}/${PN}/
}

RDEPENDS:${PN}-ptest += "\
    libnet-libidn-perl \
    liburi-perl \
    perl-module-file-glob \
    perl-module-findbin \
    perl-module-io-socket-inet \
    perl-module-io-socket-ip \
    perl-module-perlio \
    perl-module-perlio-scalar \
    perl-module-test-more \
"

do_install_ptest:append () {
    cp -r ${B}/certs ${D}${PTEST_PATH}
}

BBCLASSEXTEND = "native"
