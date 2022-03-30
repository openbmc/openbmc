SUMMARY = "ExtUtils::Helpers - Various portability utilities for module builders"
DESCRIPTION = "This module provides various portable helper function for module building modules."
SECTION = "libs"

HOMEPAGE = "http://search.cpan.org/~leont/ExtUtils-Helpers/"

LICENSE = "Artistic-1.0 | GPL-1.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=223c04045664f72c3a6556462612bddd"

SRC_URI = "${CPAN_MIRROR}/authors/id/L/LE/LEONT/ExtUtils-Helpers-${PV}.tar.gz"
SRC_URI[md5sum] = "83b00c1e401321c425ae5db6b2b2fd12"
SRC_URI[sha256sum] = "de901b6790a4557cf4ec908149e035783b125bf115eb9640feb1bc1c24c33416"

S = "${WORKDIR}/ExtUtils-Helpers-${PV}"

inherit cpan

RDEPENDS:${PN} = " perl-module-file-copy \
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
