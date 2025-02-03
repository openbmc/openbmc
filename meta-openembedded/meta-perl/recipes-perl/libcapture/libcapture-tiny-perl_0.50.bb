SUMMARY = "Capture::Tiny - Capture STDOUT and STDERR from Perl, XS or external programs."
DESCRIPTION = "Capture::Tiny provies a simple, portable way to capture \
almost anything sent to STDOUT or STDERR, regardless of whether it comes \
from Perl, from XS code or from an external program. Optionally, output can \
be teed so that it is captured while being passed through to the original \
filehandles. Yes, it even works on Windows (usually). Stop guessing which of \
a dozen capturing modules to use in any particular situation and just use \
this one."
SECTION = "libs"

HOMEPAGE = "https://metacpan.org/dist/Capture-Tiny"

LICENSE = "Artistic-1.0 | GPL-1.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=dc7698cd2954eda44d2b50be647784f8"

SRCNAME = "Capture-Tiny"
SRC_URI = "${CPAN_MIRROR}/authors/id/D/DA/DAGOLDEN/${SRCNAME}-${PV}.tar.gz"
SRC_URI[sha256sum] = "ca6e8d7ce7471c2be54e1009f64c367d7ee233a2894cacf52ebe6f53b04e81e5"

S = "${WORKDIR}/${SRCNAME}-${PV}"

inherit cpan ptest-perl

RDEPENDS:${PN} = " \
    perl-module-carp \
    perl-module-exporter \
    perl-module-extutils-makemaker \
    perl-module-extutils-mm-unix \
    perl-module-file-spec \
    perl-module-file-temp \
    perl-module-io-handle \
    perl-module-lib \
    perl-module-overloading \
    perl-module-perlio \
    perl-module-perlio-scalar \
    perl-module-scalar-util \
    perl-module-strict \
    perl-module-test-more \
    perl-module-warnings \
"

RDEPENDS:${PN}-ptest += "perl-module-perlio"

BBCLASSEXTEND = "native"
