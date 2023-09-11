SUMMARY = "Convert::ASN1 - Perl ASN.1 Encode/Decode library"
SECTION = "libs"
HOMEPAGE = "http://search.cpan.org/dist/Convert-ASN1/"
DESCRIPTION = "Convert::ASN1 is a perl library for encoding/decoding data using ASN.1 definitions."
LICENSE = "Artistic-1.0 | GPL-1.0-or-later"
LIC_FILES_CHKSUM = "file://README.md;beginline=91;endline=97;md5=ceff7fd286eb6d8e8e0d3d23e096a63f"

SRC_URI = "https://cpan.metacpan.org/authors/id/T/TI/TIMLEGGE/Convert-ASN1-${PV}.tar.gz"

SRC_URI[sha256sum] = "a628d7c9d390568fb76359975fa03f626ce57f10dc17980e8e3587d7713e4ee7"

S = "${WORKDIR}/Convert-ASN1-${PV}"

inherit cpan ptest-perl

EXTRA_PERLFLAGS = "-I ${PERLHOSTLIB}"

RDEPENDS:${PN} += "perl-module-exporter perl-module-constant perl-module-encode perl-module-encode-encoding perl-module-utf8 \
                   perl-module-socket perl-module-time-local perl-module-posix perl-module-scalar-util perl-module-test-more"
RDEPENDS:${PN}-ptest += "perl-module-math-bigint perl-module-io-socket perl-module-data-dumper perl-module-math-bigint-calc"

BBCLASSEXTEND = "native"
