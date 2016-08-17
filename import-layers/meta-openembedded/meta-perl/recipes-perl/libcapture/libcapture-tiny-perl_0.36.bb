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

LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = "file://LICENSE;md5=37a4918a30ace24395020e5b8c03b83f"

SRCNAME = "Capture-Tiny"
SRC_URI = "${CPAN_MIRROR}/authors/id/D/DA/DAGOLDEN/${SRCNAME}-${PV}.tar.gz"
SRC_URI[md5sum] = "db6444111c30ac01a76a4c118241c7b6"
SRC_URI[sha256sum] = "ab8742e53ad204a421bc82d5813f3c4c85c76581ea10d910d0aefc161f8cb03d"

S = "${WORKDIR}/${SRCNAME}-${PV}"

inherit cpan

RDEPENDS_${PN} = " perl-module-scalar-util \
                   perl-module-io-file \
                   perl-module-extutils-makemaker \
                   perl-module-file-spec \
                   perl-module-exporter \
                   perl-module-carp \
                   perl-module-test-more \
                   perl-module-file-temp \
                   perl-module-lib \
                   perl-module-overloading \
"

BBCLASSEXTEND = "native"
