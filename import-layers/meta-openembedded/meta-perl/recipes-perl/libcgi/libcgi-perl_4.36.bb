DESCRIPTION = "CGI.pm is a stable, complete and mature solution for processing and preparing \
HTTP requests and responses. Major features including processing form \
submissions, file uploads, reading and writing cookies, query string generation \
and manipulation, and processing and preparing HTTP headers."
HOMEPAGE = "http://search.cpan.org/~leejo/CGI-4.28/lib/CGI.pod"
SECTION = "libs"
LICENSE = "Artistic-2.0 | GPL-2.0"

LIC_FILES_CHKSUM = "file://LICENSE;md5=2e9769f0a2613a98bc7fce15dee0c533"

SRC_URI = "http://search.cpan.org/CPAN/authors/id/L/LE/LEEJO/CGI-${PV}.tar.gz"

SRC_URI[md5sum] = "48566d81f430a7f755bb4d8f426ce35d"
SRC_URI[sha256sum] = "fefe84d4f2461e867f9be80f3f988e17bcbbcb4e306952cf2fd1dea7e4515490"

S = "${WORKDIR}/CGI-${PV}"

inherit cpan

RPROVIDES_${PN} += "perl-module-cgi"
