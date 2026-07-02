SUMMARY = "ExtUtils::ParseXS - converts Perl XS code into C code"
DESCRIPTION = "\"ExtUtils::ParseXS\" will compile XS code into C code by \
embedding the constructs necessary to let C functions manipulate Perl \
values and creates the glue necessary to let Perl access those functions. \
The compiler uses typesmapes to determine how to map C function parameters \
and variables to Perl values."

SECTION = "libs"

HOMEPAGE = "https://metacpan.org/release/SMUELLER/ExtUtils-ParseXS-3.35"

LICENSE = "Artistic-1.0 | GPL-1.0-or-later"
LIC_FILES_CHKSUM = "file://README;beginline=120;endline=129;md5=eb858f0e3b1b0bee0c05b86a474ae2b6"

SRCNAME = "ExtUtils-ParseXS"
SRC_URI = "${CPAN_MIRROR}/authors/id/L/LE/LEONT/${SRCNAME}-${PV}.tar.gz"
SRC_URI[sha256sum] = "d19a3f29288f0950ef8f1838db99270284ba475758246f0e5ab1113a9d9a7548"

S = "${UNPACKDIR}/${SRCNAME}-${PV}"

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
