SUMMARY = "MIME::Types - Definition of MIME types"
DESCRIPTION = "MIME types are used in MIME compliant lines, for instance \
as part of e-mail and HTTP traffic, to indicate the type of content which \
is transmitted. Sometimes real knowledge about a mime-type is need.\
\n\
This module maintains a set of MIME::Type objects, which each describe \
one known mime type."
HOMEPAGE = "http://search.cpan.org/~markov/MIME-Types-${PV}"
SECTION = "libraries"

LICENSE = "Artistic-1.0|GPLv1+"
LIC_FILES_CHKSUM = "file://META.yml;beginline=11;endline=11;md5=963ce28228347875ace682de56eef8e8"

SRC_URI = "http://search.cpan.org/CPAN/authors/id/M/MA/MARKOV/MIME-Types-${PV}.tar.gz \
           file://run-ptest \
          "
SRC_URI[md5sum] = "6f1441addab947137bac92c379a47ba3"
SRC_URI[sha256sum] = "e04ed7d42f1ff3150a303805f2689c28f80b92c511784d4641cb7f040d3e8ff6"

S = "${WORKDIR}/MIME-Types-${PV}"

inherit cpan ptest

RDEPENDS_${PN} = "\
    perl-module-base \
    perl-module-file-basename \
    perl-module-file-spec \
    perl-module-overload \
"

RDEPENDS_${PN}-ptest = "perl-module-lib perl-module-test-more"
#RSUGGESTS_${PN}-ptest = "libmojo-base-perl"

do_install () {
    cpan_do_install
    install -d ${D}${bindir}
    install -m 755 ${S}/bin/collect-types ${D}${bindir}/collect-types
}

do_install_ptest () {
    cp -r ${B}/t ${D}${PTEST_PATH}
}
