SUMMARY = "Convert::ASN1 - Perl ASN.1 Encode/Decode library"
SECTION = "libs"
HOMEPAGE = "http://search.cpan.org/dist/Convert-ASN1/"
DESCRIPTION = "Convert::ASN1 is a perl library for encoding/decoding data using ASN.1 definitions."
LICENSE = "Artistic-1.0 | GPL-1.0-or-later"
LIC_FILES_CHKSUM = "file://README.md;beginline=91;endline=97;md5=ceff7fd286eb6d8e8e0d3d23e096a63f"

SRC_URI = "https://cpan.metacpan.org/authors/id/T/TI/TIMLEGGE/Convert-ASN1-${PV}.tar.gz"

SRC_URI[sha256sum] = "1fdf004520c79e3a244cf9688616293516c11793d746c761f367496eb3d06076"

S = "${WORKDIR}/Convert-ASN1-${PV}"

inherit cpan ptest-perl

EXTRA_PERLFLAGS = "-I ${PERLHOSTLIB}"

RDEPENDS:${PN} += "perl-module-exporter perl-module-constant perl-module-encode perl-module-encode-encoding perl-module-utf8 perl-module-socket perl-module-time-local perl-module-posix"
RDEPENDS:${PN}-ptest += "perl-module-math-bigint perl-module-io-socket perl-module-data-dumper perl-module-math-bigint-calc"

BBCLASSEXTEND = "native"
