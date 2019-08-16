SUMMARY = "Sub::Uplevel - apparently run a function in a higher stack frame"
DESCRIPTION = " Like Tcl's uplevel() function, but not quite so dangerous. \
The idea is just to fool caller(). All the really naughty bits of Tcl's \
uplevel() are avoided. \
\
THIS IS NOT THE SORT OF THING YOU WANT TO DO EVERYDAY \
"

SECTION = "libs"
HOMEPAGE= "https://metacpan.org/release/Sub-Uplevel"

LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7f1207cd3108e4ade18448d81e6bcb6c"

CPAN_PACKAGE = "Sub-Uplevel"
CPAN_AUTHOR = "DAGOLDEN"

SRC_URI = "${CPAN_MIRROR}/authors/id/D/DA/${CPAN_AUTHOR}/${CPAN_PACKAGE}-${PV}.tar.gz"

SRC_URI[md5sum] = "6c6a174861fd160e8d5871a86df00baf"
SRC_URI[sha256sum] = "b4f3f63b80f680a421332d8851ddbe5a8e72fcaa74d5d1d98f3c8cc4a3ece293"

S = "${WORKDIR}/${CPAN_PACKAGE}-${PV}"

inherit cpan ptest-perl

RDEPENDS_${PN} += " \
    perl-module-carp \
    perl-module-constant \
    perl-module-strict \
    perl-module-warnings \
"

RDEPENDS_${PN}-ptest += " \
    perl-module-cpan \
    perl-module-exporter \
    perl-module-extutils-makemaker \
    perl-module-file-spec \
    perl-module-lib \
    perl-module-test-more \
"

BBCLASSEXTEND = "native nativesdk"
