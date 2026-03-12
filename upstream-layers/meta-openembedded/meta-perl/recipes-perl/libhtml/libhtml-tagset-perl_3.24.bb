SUMMARY = "HTML::Tageset -  data tables useful in parsing HTML"
DESCRIPTION = "This module contains several data tables useful in various \
kinds of HTML parsing operations."
HOMEPAGE = "https://metacpan.org/release/HTML-Tagset"
SECTION = "libs"
LICENSE = "Artistic-2.0"

LIC_FILES_CHKSUM = "file://README.md;beginline=42;endline=46;md5=6e702d3d2c184c6972123c9bef7b29b7"

SRC_URI = "${CPAN_MIRROR}/authors/id/P/PE/PETDANCE/HTML-Tagset-${PV}.tar.gz"
SRC_URI[sha256sum] = "eb89e145a608ed1f8f141a57472ee5f69e67592a432dcd2e8b1dbb445f2b230b"

S = "${UNPACKDIR}/HTML-Tagset-${PV}"


inherit cpan ptest-perl


RDEPENDS:${PN} += "perl-module-strict perl-module-vars"

RDEPENDS:${PN}-ptest += "perl-module-test perl-module-test-more"

do_install:prepend() {
    # requires "-T" (taint) command line option
    rm -rf ${B}/t/pod.t
}

BBCLASSEXTEND = "native"
