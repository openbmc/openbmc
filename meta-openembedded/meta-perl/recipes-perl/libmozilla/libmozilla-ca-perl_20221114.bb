SUMMARY = "Mozilla's CA cert bundle in PEM format"
DESCRIPTION = "Mozilla::CA provides a copy of Mozilla's bundle of \
Certificate Authority certificates in a form that can be consumed by  \
modules and libraries based on OpenSSL."
HOMEPAGE = "https://metacpan.org/pod/Mozilla::CA"
BUGTRACKER = "https://github.com/libwww-perl/Mozilla-CA/issues"
SECTION = "libs"

LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://README;beginline=32;endline=39;md5=51e666dce556490a1132e937ad3f8729"

SRC_URI = "${CPAN_MIRROR}/authors/id/H/HA/HAARG/Mozilla-CA-${PV}.tar.gz"
SRC_URI[sha256sum] = "701bea67be670add5a102f9f8c879402b4983096b1cb0e20dd47d52d7a10666b"

S = "${WORKDIR}/Mozilla-CA-${PV}"

inherit cpan ptest-perl

RDEPENDS:${PN}-ptest += "\
    perl-module-test-more \
"
BBCLASSEXTEND = "native"
