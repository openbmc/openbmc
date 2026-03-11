SUMMARY = "Mozilla's CA cert bundle in PEM format"
DESCRIPTION = "Mozilla::CA provides a copy of Mozilla's bundle of \
Certificate Authority certificates in a form that can be consumed by  \
modules and libraries based on OpenSSL."
HOMEPAGE = "https://metacpan.org/pod/Mozilla::CA"
BUGTRACKER = "https://github.com/libwww-perl/Mozilla-CA/issues"
SECTION = "libs"

LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://README;beginline=32;endline=39;md5=51e666dce556490a1132e937ad3f8729"

SRC_URI = "${CPAN_MIRROR}/authors/id/L/LW/LWP/Mozilla-CA-${PV}.tar.gz"
SRC_URI[sha256sum] = "adeac0752440b2da094e8036bab6c857e22172457658868f5ac364f0c7b35481"

S = "${UNPACKDIR}/Mozilla-CA-${PV}"

inherit cpan ptest-perl

RDEPENDS:${PN}-ptest += "\
    perl-module-test-more \
"
BBCLASSEXTEND = "native"
