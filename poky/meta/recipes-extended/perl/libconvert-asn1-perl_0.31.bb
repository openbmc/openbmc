SUMMARY = "Convert::ASN1 - Perl ASN.1 Encode/Decode library"
SECTION = "libs"
HOMEPAGE = "http://search.cpan.org/dist/Convert-ASN1/"
DESCRIPTION = "Convert::ASN1 is a perl library for encoding/decoding data using ASN.1 definitions."
LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = "file://README.md;beginline=91;endline=97;md5=ceff7fd286eb6d8e8e0d3d23e096a63f"

SRC_URI = "https://cpan.metacpan.org/authors/id/T/TI/TIMLEGGE/Convert-ASN1-${PV}.tar.gz"

SRC_URI[md5sum] = "1e12b263a5042804bb1c59ddce899876"
SRC_URI[sha256sum] = "6fe4c1ba744c3a8212bf2c9b2703d93530acc153435cf2f93633540b439fbbeb"

S = "${WORKDIR}/Convert-ASN1-${PV}"

inherit cpan ptest-perl

EXTRA_PERLFLAGS = "-I ${PERLHOSTLIB}"

RDEPENDS:${PN} += "perl-module-exporter perl-module-constant perl-module-encode perl-module-encode-encoding perl-module-utf8 perl-module-socket perl-module-time-local perl-module-posix"
RDEPENDS:${PN}-ptest += "perl-module-math-bigint perl-module-io-socket perl-module-data-dumper perl-module-math-bigint-calc"

BBCLASSEXTEND = "native"
