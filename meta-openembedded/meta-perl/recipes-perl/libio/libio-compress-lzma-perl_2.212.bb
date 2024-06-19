SUMMARY = "Perl interface to allow reading and writing of lzma files/buffers."
DESCRIPTION = "This module provides a Perl interface to allow reading and \
writing of lzma files/buffers."
HOMEPAGE = "https://metacpan.org/release/IO-Compress-Lzma"
SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0-or-later"

LIC_FILES_CHKSUM = "file://README;beginline=8;endline=10;md5=2a0fd6d30eaf88289587b776f74c2886"

SRC_URI = "${CPAN_MIRROR}/authors/id/P/PM/PMQS/IO-Compress-Lzma-${PV}.tar.gz"

SRC_URI[sha256sum] = "51ef81f931cfd2c13fb2340c665204a9587843087dec1a57e9e9f4666d6dad40"

S = "${WORKDIR}/IO-Compress-Lzma-${PV}"

inherit cpan

RDEPENDS:${PN} += "\
    perl-module-autoloader \
    libcompress-raw-lzma-perl \
    libio-compress-perl \
"

BBCLASSEXTEND = "native"
