SUMMARY = "A simple, sane and efficient module to slurp a file"
DESCRIPTION = "This module provides functions for fast and correct slurping and spewing. \
All functions are optionally exported. All functions throw exceptions on \
errors, write functions don't return any meaningful value."

SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0-or-later"

HOMEPAGE=       "https://metacpan.org/release/File-Slurper"

LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Artistic-1.0;md5=cda03bbdc3c1951996392b872397b798 \
file://${COMMON_LICENSE_DIR}/GPL-1.0-or-later;md5=30c0b8a5048cc2f4be5ff15ef0d8cf61"

SRC_URI = "${CPAN_MIRROR}/authors/id/L/LE/LEONT/File-Slurper-${PV}.tar.gz"

SRC_URI[sha256sum] = "d5a36487339888c3cd758e648160ee1d70eb4153cacbaff57846dbcefb344b0c"
RDEPENDS:${PN} = " \
    perl-module-carp \
    perl-module-encode \
    perl-module-exporter \
    perl-module-perlio \
    perl-module-perlio-encoding \
    perl-module-strict \
    perl-module-warnings \
"

RDEPENDS:${PN}-ptest += "libtest-warnings-perl \
    perl-module-test-more \
    "

S = "${WORKDIR}/File-Slurper-${PV}"

inherit cpan ptest-perl

BBCLASSEXTEND = "native"
