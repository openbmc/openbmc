DESCRIPTION = "CGI.pm is a stable, complete and mature solution for processing and preparing \
HTTP requests and responses. Major features including processing form \
submissions, file uploads, reading and writing cookies, query string generation \
and manipulation, and processing and preparing HTTP headers."
HOMEPAGE = "http://search.cpan.org/~leejo/CGI-4.28/lib/CGI.pod"
SECTION = "libs"
LICENSE = "Artistic-2.0 | GPL-2.0"

LIC_FILES_CHKSUM = "file://LICENSE;md5=2e9769f0a2613a98bc7fce15dee0c533"

SRC_URI = "http://search.cpan.org/CPAN/authors/id/L/LE/LEEJO/CGI-${PV}.tar.gz"

SRC_URI[md5sum] = "0aeb8563d533e7f83724ed068b5bfc37"
SRC_URI[sha256sum] = "8c58f4a529bb92a914b22b7e64c5e31185c9854a4070a6dfad44fe5cc248e7d4"

S = "${WORKDIR}/CGI-${PV}"

inherit cpan

RDEPENDS_${PN} += "\
    perl-module-deprecate \
    perl-module-if \
"

RPROVIDES_${PN} += "perl-module-cgi"
