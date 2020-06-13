DESCRIPTION = "CGI.pm is a stable, complete and mature solution for processing and preparing \
HTTP requests and responses. Major features including processing form \
submissions, file uploads, reading and writing cookies, query string generation \
and manipulation, and processing and preparing HTTP headers."
HOMEPAGE = "http://search.cpan.org/~leejo/CGI-4.28/lib/CGI.pod"
SECTION = "libs"
LICENSE = "Artistic-2.0 | GPL-2.0"

LIC_FILES_CHKSUM = "file://LICENSE;md5=415fc49abed2728f9480cd32c8d67beb"

SRC_URI = "http://search.cpan.org/CPAN/authors/id/L/LE/LEEJO/CGI-${PV}.tar.gz"

SRC_URI[md5sum] = "0e81952bb42e00361abc4d33696b08f5"
SRC_URI[sha256sum] = "102cd7562983b85e8354ebcbb3be94a35f7f6706b2b49fdb790135da4d1d55d2"

S = "${WORKDIR}/CGI-${PV}"

inherit cpan ptest-perl

RDEPENDS_${PN} += "\
    libhtml-parser-perl \
    perl-module-base \
    perl-module-deprecate \
    perl-module-if \
"

do_install_prepend() {
    # requires "-T" (taint) command line option
    rm -rf ${B}/t/push.t
    rm -rf ${B}/t/utf8.t
    # tests building of docs
    rm -rf ${B}/t/compiles_pod.t
}

RDEPENDS_${PN}-ptest += " \
    libtest-deep-perl \
    libtest-warn-perl \
    perl-module-bytes \
    perl-module-file-find \
    perl-module-filehandle \
    perl-module-findbin \
    perl-module-lib \
    perl-module-perlio \
    perl-module-perlio-scalar \
    perl-module-test-more \
    perl-module-utf8 \
"

RPROVIDES_${PN} += "perl-module-cgi"

BBCLASSEXTEND = "native"
