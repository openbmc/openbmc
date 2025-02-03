DESCRIPTION = "CGI.pm is a stable, complete and mature solution for processing and preparing \
HTTP requests and responses. Major features including processing form \
submissions, file uploads, reading and writing cookies, query string generation \
and manipulation, and processing and preparing HTTP headers."
HOMEPAGE = "https://metacpan.org/pod/CGI"
SECTION = "libs"
LICENSE = "Artistic-2.0 | GPL-2.0-only"

LIC_FILES_CHKSUM = "file://LICENSE;md5=415fc49abed2728f9480cd32c8d67beb"

SRC_URI = "${CPAN_MIRROR}/authors/id/L/LE/LEEJO/CGI-${PV}.tar.gz"

SRC_URI[sha256sum] = "f4a6896eb94a3ecaa1c1ba02f7e0d2bed0be4c5ad3378d80196ec25662ac4111"

S = "${WORKDIR}/CGI-${PV}"

inherit cpan ptest-perl

RDEPENDS:${PN} += "\
    libhtml-parser-perl \
    perl-module-base \
    perl-module-deprecate \
    perl-module-if \
"

do_install:prepend() {
    # requires "-T" (taint) command line option
    rm -rf ${B}/t/push.t
    rm -rf ${B}/t/utf8.t
    # tests building of docs
    rm -rf ${B}/t/compiles_pod.t
}

RDEPENDS:${PN}-ptest += " \
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

RPROVIDES:${PN} += "perl-module-cgi"

BBCLASSEXTEND = "native"
