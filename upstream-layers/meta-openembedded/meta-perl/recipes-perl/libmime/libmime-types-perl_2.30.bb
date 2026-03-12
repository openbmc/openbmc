SUMMARY = "MIME::Types - Definition of MIME types"
DESCRIPTION = "MIME types are used in MIME compliant lines, for instance \
as part of e-mail and HTTP traffic, to indicate the type of content which \
is transmitted. Sometimes real knowledge about a mime-type is need.\
\n\
This module maintains a set of MIME::Type objects, which each describe \
one known mime type."
HOMEPAGE = "https://metacpan.org/release/MARKOV/MIME-Types-2.27"
SECTION = "libraries"

LICENSE = "Artistic-1.0 | GPL-1.0-or-later"
LIC_FILES_CHKSUM = "file://META.yml;beginline=13;endline=13;md5=963ce28228347875ace682de56eef8e8"

SRC_URI = "${CPAN_MIRROR}/authors/id/M/MA/MARKOV/MIME-Types-${PV}.tar.gz \
           file://run-ptest \
          "
SRC_URI[sha256sum] = "f31b1666bdf420b4b65c373ce0129ee349dd24bab4cd16c7f01b698fe450be6f"

S = "${UNPACKDIR}/MIME-Types-${PV}"

inherit cpan ptest

RDEPENDS:${PN} = "\
    perl-module-base \
    perl-module-carp \
    perl-module-constant \
    perl-module-cwd \
    perl-module-encode-encoding \
    perl-module-file-basename \
    perl-module-file-spec \
    perl-module-list-util \
    perl-module-overload \
    perl-module-perlio \
    perl-module-perlio-encoding \
"

RDEPENDS:${PN}-ptest = "\
    libmime-types-perl \
    perl-module-lib \
    perl-module-test-more \
"

#RSUGGESTS:${PN}-ptest = "libmojo-base-perl"

do_install_ptest () {
    cp -r ${B}/t ${D}${PTEST_PATH}
}
