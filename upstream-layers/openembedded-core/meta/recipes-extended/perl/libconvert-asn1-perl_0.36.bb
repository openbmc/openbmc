SUMMARY = "Convert::ASN1 - Perl ASN.1 Encode/Decode library"
SECTION = "libs"
HOMEPAGE = "https://metacpan.org/dist/Convert-ASN1/"
DESCRIPTION = "Convert::ASN1 is a perl library for encoding/decoding data using ASN.1 definitions."
LICENSE = "Artistic-1.0 OR GPL-1.0-or-later"
LIC_FILES_CHKSUM = "file://README.md;beginline=91;endline=97;md5=ceff7fd286eb6d8e8e0d3d23e096a63f"

SRC_URI = "${CPAN_MIRROR}/authors/id/T/TI/TIMLEGGE/Convert-ASN1-${PV}.tar.gz"

SRC_URI[sha256sum] = "50439aedff4b403f24c9373eefd3c25770e33a9190d93a331e7a744f8b4e7cd3"

S = "${UNPACKDIR}/Convert-ASN1-${PV}"

inherit cpan ptest-perl

EXTRA_PERLFLAGS = "-I ${PERLHOSTLIB}"

RDEPENDS:${PN} += "perl-module-exporter perl-module-constant perl-module-encode perl-module-encode-encoding perl-module-utf8 \
                   perl-module-socket perl-module-time-local perl-module-posix perl-module-scalar-util perl-module-test-more"
RDEPENDS:${PN}-ptest += "perl-module-math-bigint perl-module-io-socket perl-module-data-dumper perl-module-math-bigint-calc"

BBCLASSEXTEND = "native"
