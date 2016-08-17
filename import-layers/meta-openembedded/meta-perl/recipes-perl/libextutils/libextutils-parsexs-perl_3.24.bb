SUMMARY = "ExtUtils::ParseXS - converts Perl XS code into C code"
DESCRIPTION = "\"ExtUtils::ParseXS\" will compile XS code into C code by \
embedding the constructs necessary to let C functions manipulate Perl \
values and creates the glue necessary to let Perl access those functions. \
The compiler uses typesmapes to determine how to map C function parameters \
and variables to Perl values."

SECTION = "libs"

HOMEPAGE = "http://metapan.org/release/ExtUtils-ParseXS/"

LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = "file://README;beginline=121;endline=130;md5=10ddb3a673b4c732022ac899968ea9cc"

SRCNAME = "ExtUtils-ParseXS"
SRC_URI = "${CPAN_MIRROR}/authors/id/S/SM/SMUELLER/${SRCNAME}-${PV}.tar.gz"
SRC_URI[md5sum] = "e6be3f1d493e04ed805576104cf4328b"
SRC_URI[sha256sum] = "30b60b8208fc9b7746ed934b678bb9618a8f28994dae8774548353a7b550371e"

S = "${WORKDIR}/${SRCNAME}-${PV}"

inherit cpan

RDEPENDS_${PN} = " perl-module-carp \
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

RPROVIDES_${PN} += " libextutils-parsexs-constants-perl \
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
