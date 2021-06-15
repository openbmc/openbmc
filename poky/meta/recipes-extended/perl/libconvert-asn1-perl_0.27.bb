SUMMARY = "Convert::ASN1 - Perl ASN.1 Encode/Decode library"
SECTION = "libs"
HOMEPAGE = "https://metacpan.org/source/GBARR/Convert-ASN1-0.27"
DESCRIPTION = "Convert::ASN1 is a perl library for encoding/decoding data using ASN.1 definitions."
LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = "file://README.md;beginline=91;endline=97;md5=ceff7fd286eb6d8e8e0d3d23e096a63f"

SRC_URI = "http://search.cpan.org/CPAN/authors/id/G/GB/GBARR/Convert-ASN1-${PV}.tar.gz"

SRC_URI[md5sum] = "68723e96be0b258a9e20480276e8a62c"
SRC_URI[sha256sum] = "74a4a78ae0c5e973100ac0a8f203a110f76fb047b79dae4fc1fd7d6814d3d58a"

S = "${WORKDIR}/Convert-ASN1-${PV}"

inherit cpan ptest-perl

EXTRA_PERLFLAGS = "-I ${PERLHOSTLIB}"

RDEPENDS_${PN} += "perl-module-exporter perl-module-constant perl-module-encode perl-module-encode-encoding perl-module-utf8 perl-module-socket perl-module-time-local perl-module-posix"
RDEPENDS_${PN}-ptest += "perl-module-math-bigint perl-module-io-socket perl-module-data-dumper perl-module-math-bigint-calc"

BBCLASSEXTEND = "native"
