SUMMARY = "Capture::Tiny - Capture STDOUT and STDERR from Perl, XS or external programs."
DESCRIPTION = "Capture::Tiny provies a simple, portable way to capture \
almost anything sent to STDOUT or STDERR, regardless of whether it comes \
from Perl, from XS code or from an external program. Optionally, output can \
be teed so that it is captured while being passed through to the original \
filehandles. Yes, it even works on Windows (usually). Stop guessing which of \
a dozen capturing modules to use in any particular situation and just use \
this one."
SECTION = "libs"

HOMEPAGE = "http://search.cpan.org/~dagolden/Capture-Tiny/"

LICENSE = "Artistic-1.0 | GPL-1.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=37a4918a30ace24395020e5b8c03b83f"

SRCNAME = "Capture-Tiny"
SRC_URI = "${CPAN_MIRROR}/authors/id/D/DA/DAGOLDEN/${SRCNAME}-${PV}.tar.gz"
SRC_URI[md5sum] = "f5d24083ad270f8326dd659dd83eeb54"
SRC_URI[sha256sum] = "6c23113e87bad393308c90a207013e505f659274736638d8c79bac9c67cc3e19"

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
