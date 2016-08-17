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
LIC_FILES_CHKSUM = "file://META.yml;beginline=11;endline=11;md5=963ce28228347875ace682de56eef8e8"

RDEPENDS_${PN} += "perl-module-scalar-util libnet-ssleay-perl"

SRC_URI = "http://search.cpan.org/CPAN/authors/id/S/SU/SULLR/IO-Socket-SSL-${PV}.tar.gz \
           file://run-ptest \
          "
SRC_URI[md5sum] = "2a6268bb42da81e69d1c1feb2fcb0eea"
SRC_URI[sha256sum] = "acdb67b5b63aea5b7e70c3e4c70a16128810329592b63753a38c794aff76a1dd"

S = "${WORKDIR}/IO-Socket-SSL-${PV}"

inherit cpan ptest

do_install_append () {
    mkdir -p ${D}${docdir}/${PN}/
    cp ${S}/BUGS ${D}${docdir}/${PN}/
    cp ${S}/Changes ${D}${docdir}/${PN}/
    cp ${S}/README ${D}${docdir}/${PN}/
    cp -pRP ${S}/docs ${D}${docdir}/${PN}/
    cp -pRP ${S}/certs ${D}${docdir}/${PN}/
    cp -pRP ${S}/example ${D}${docdir}/${PN}/
    cp -pRP ${S}/util ${D}${docdir}/${PN}/
}

do_install_ptest () {
    cp -r ${B}/t ${D}${PTEST_PATH}
    cp -r ${B}/certs ${D}${PTEST_PATH}
}

BBCLASSEXTEND = "native"
