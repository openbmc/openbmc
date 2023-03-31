SUMMARY = "Perl interface to allow reading and writing of lzma files/buffers."
DESCRIPTION = "This module provides a Perl interface to allow reading and \
writing of lzma files/buffers."
HOMEPAGE = "https://metacpan.org/release/IO-Compress-Lzma"
SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0-or-later"

LIC_FILES_CHKSUM = "file://README;beginline=8;endline=10;md5=b95311d4a7dbf3d0d3663edc094aced6"

SRC_URI = "${CPAN_MIRROR}/authors/id/P/PM/PMQS/IO-Compress-Lzma-${PV}.tar.gz"

SRC_URI[sha256sum] = "1e7a4c4f4e665434df59246b19d168b08af2f1aeebaa08937bd69ad3e5e319c8"

S = "${WORKDIR}/IO-Compress-Lzma-${PV}"

inherit cpan

RDEPENDS:${PN} += "\
    perl-module-autoloader \
    libcompress-raw-lzma-perl \
    libio-compress-perl \
"

BBCLASSEXTEND = "native"
