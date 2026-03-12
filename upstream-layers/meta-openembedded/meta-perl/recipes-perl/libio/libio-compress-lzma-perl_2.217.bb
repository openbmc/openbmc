SUMMARY = "Perl interface to allow reading and writing of lzma files/buffers."
DESCRIPTION = "This module provides a Perl interface to allow reading and \
writing of lzma files/buffers."
HOMEPAGE = "https://metacpan.org/release/IO-Compress-Lzma"
SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0-or-later"

LIC_FILES_CHKSUM = "file://README;beginline=8;endline=10;md5=dcf5ec9168367166aaeabb92ee5754e1"

SRC_URI = "${CPAN_MIRROR}/authors/id/P/PM/PMQS/IO-Compress-Lzma-${PV}.tar.gz"

SRC_URI[sha256sum] = "3462ecd1e67e85d5e4fa911bc6d8e38a884ba1d6e90a03535f0d28fe2ad0aacf"

S = "${UNPACKDIR}/IO-Compress-Lzma-${PV}"

inherit cpan

RDEPENDS:${PN} += "\
    perl-module-autoloader \
    libcompress-raw-lzma-perl \
    libio-compress-perl \
"

BBCLASSEXTEND = "native"
