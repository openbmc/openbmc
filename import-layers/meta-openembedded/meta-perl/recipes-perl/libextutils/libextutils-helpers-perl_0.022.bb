SUMMARY = "ExtUtils::Helpers - Various portability utilities for module builders"
DESCRIPTION = "This module provides various portable helper function for module building modules."
SECTION = "libs"

HOMEPAGE = "http://search.cpan.org/~leont/ExtUtils-Helpers/"

LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = "file://LICENSE;md5=307057ce232899f5caa8858560c7274b"

SRC_URI = "${CPAN_MIRROR}/authors/id/L/LE/LEONT/ExtUtils-Helpers-${PV}.tar.gz"
SRC_URI[md5sum] = "cf4fd6f8caa6daac33b1111c9e93162b"
SRC_URI[sha256sum] = "d3f8cf700fb3414ca1260089755cbf64041455e4b744110677b1ba5bb9a3aa95"

S = "${WORKDIR}/ExtUtils-Helpers-${PV}"

inherit cpan

RDEPENDS_${PN} = " perl-module-file-copy \
                   perl-module-extutils-makemaker \
                   perl-module-exporter \
                   perl-module-carp \
                   perl-module-test-more \
                   perl-module-text-parsewords \
                   perl-module-load \
                   perl-module-file-temp \
                   perl-module-file-spec-functions \
"

BBCLASSEXTEND = "native"
