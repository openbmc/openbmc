DESCRIPTION = "CGI.pm is a stable, complete and mature solution for processing and preparing \
HTTP requests and responses. Major features including processing form \
submissions, file uploads, reading and writing cookies, query string generation \
and manipulation, and processing and preparing HTTP headers."
HOMEPAGE = "http://search.cpan.org/~leejo/CGI-4.28/lib/CGI.pod"
SECTION = "libs"
LICENSE = "Artistic-2.0 | GPL-2.0"

LIC_FILES_CHKSUM = "file://LICENSE;md5=2e9769f0a2613a98bc7fce15dee0c533"

SRC_URI = "http://search.cpan.org/CPAN/authors/id/L/LE/LEEJO/CGI-${PV}.tar.gz"

SRC_URI[md5sum] = "28efb391377f6e98c19c23292d5fcc8c"
SRC_URI[sha256sum] = "1297d3ed6616cacb4eb57860e3e743f3890111e7a63ca08849930f42f1360532"

S = "${WORKDIR}/CGI-${PV}"

inherit cpan

RPROVIDES_${PN} += "perl-module-cgi"
