SUMMARY = "MIME::Base32 - Base32 encoder and decoder"
DESCRIPTION = "This module is for encoding/decoding data much the way that MIME::Base64 does."
HOMEPAGE = "https://metacpan.org/release/REHSACK/MIME-Base32-1.303"
SECTION = "libraries"

LICENSE = "Artistic-1.0 | GPL-1.0-or-later"
LIC_FILES_CHKSUM = "file://META.yml;beginline=11;endline=11;md5=963ce28228347875ace682de56eef8e8"

SRC_URI = "${CPAN_MIRROR}/authors/id/R/RE/REHSACK/MIME-Base32-${PV}.tar.gz"
SRC_URI[sha256sum] = "ab21fa99130e33a0aff6cdb596f647e5e565d207d634ba2ef06bdbef50424e99"

S = "${UNPACKDIR}/MIME-Base32-${PV}"

inherit cpan ptest-perl

RDEPENDS:${PN}-ptest += "perl-module-test-more"
