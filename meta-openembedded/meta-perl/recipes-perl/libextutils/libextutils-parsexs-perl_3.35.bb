SUMMARY = "ExtUtils::ParseXS - converts Perl XS code into C code"
DESCRIPTION = "\"ExtUtils::ParseXS\" will compile XS code into C code by \
embedding the constructs necessary to let C functions manipulate Perl \
values and creates the glue necessary to let Perl access those functions. \
The compiler uses typesmapes to determine how to map C function parameters \
and variables to Perl values."

SECTION = "libs"

HOMEPAGE = "http://metapan.org/release/ExtUtils-ParseXS/"

LICENSE = "Artistic-1.0 | GPL-1.0-or-later"
LIC_FILES_CHKSUM = "file://README;beginline=120;endline=129;md5=eb858f0e3b1b0bee0c05b86a474ae2b6"

SRCNAME = "ExtUtils-ParseXS"
SRC_URI = "${CPAN_MIRROR}/authors/id/S/SM/SMUELLER/${SRCNAME}-${PV}.tar.gz"
SRC_URI[md5sum] = "2ae41036d85e98e1369645724962dd16"
SRC_URI[sha256sum] = "41def0511278a2a8ba9afa25ccab45b0453f75e7fd774e8644b5f9a57cc4ee1c"

S = "${WORKDIR}/${SRCNAME}-${PV}"

inherit cpan

RDEPENDS:${PN} += " perl-module-carp \
    perl-module-cwd \
    perl-module-dynaloader \
    perl-module-extutils-cbuilder \
    perl-module-extutils-makemaker \
    perl-module-file-basename \
    perl-module-file-spec \
    perl-module-lib \
    perl-module-symbol \
    perl-module-test-more \
"

RPROVIDES:${PN} += " libextutils-parsexs-constants-perl \
    libextutils-parsexs-countlines-perl \
    libextutils-parsexs-eval-perl \
    libextutils-parsexs-utilities-perl \
    libextutils-typemaps-perl \
    libextutils-typemaps-cmd-perl \
    libextutils-typemaps-inputmap-perl \
    libextutils-typemaps-outputmap-perl \
    libextutils-typemaps-type-perl \
"

BBCLASSEXTEND = "native"
