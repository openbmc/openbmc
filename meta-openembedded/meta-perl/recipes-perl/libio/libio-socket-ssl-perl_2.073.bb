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

LICENSE = "Artistic-1.0|GPLv1+"
LIC_FILES_CHKSUM = "file://META.yml;beginline=12;endline=12;md5=963ce28228347875ace682de56eef8e8"

RDEPENDS:${PN} += "\
    libnet-ssleay-perl \
    perl-module-autoloader \
    perl-module-scalar-util \
    perl-module-io-socket \
"

SRC_URI = "http://search.cpan.org/CPAN/authors/id/S/SU/SULLR/IO-Socket-SSL-${PV}.tar.gz \
           file://run-ptest \
          "
SRC_URI[sha256sum] = "b2c0b34df97cb1aa816221cee2454a1efd89b86ccbda810389a30e0d08cf57c8"

S = "${WORKDIR}/IO-Socket-SSL-${PV}"

inherit cpan ptest

do_install:append () {
    mkdir -p ${D}${docdir}/${PN}/
    cp ${S}/BUGS ${D}${docdir}/${PN}/
    cp ${S}/Changes ${D}${docdir}/${PN}/
    cp ${S}/README ${D}${docdir}/${PN}/
    cp -pRP ${S}/docs ${D}${docdir}/${PN}/
    cp -pRP ${S}/certs ${D}${docdir}/${PN}/
    cp -pRP ${S}/example ${D}${docdir}/${PN}/
}

do_install_ptest () {
    cp -r ${B}/t ${D}${PTEST_PATH}
    cp -r ${B}/certs ${D}${PTEST_PATH}
}

BBCLASSEXTEND = "native"
