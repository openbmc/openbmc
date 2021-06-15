SUMMARY = "Test::Deep - Extremely flexible deep comparison"
DESCRIPTION = "If you don't know anything about automated testing in Perl \
then you should probably read about Test::Simple and Test::More before \
preceding. Test::Deep uses the Test::Builder framework. \
\ 
Test::Deep gives you very flexible ways to check that the result you got is \
the result you were expecting. At its simplest it compares two structures \
by going through each level, ensuring that the values match, that arrays and \
hashes have the same elements and that references are blessed into the \
correct class. It also handles circular data structures without getting \
caught in an infinite loop. \
\
Where it becomes more interesting is in allowing you to do something besides \
simple exact comparisons. With strings, the \'eq\' operator checks that 2 \
strings are exactly equal but sometimes that's not what you want. When you \
don't know exactly what the string should be but you do know some things \
about how it should look, \'eq\' is no good and you must use pattern matching \
instead. Test::Deep provides pattern matching for complex data structures \
distribution."

SECTION = "libs"

HOMEPAGE = "http://github.com/rjbs/Test-Deep/"

LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = "file://lib/Test/Deep.pm;beginline=1817;endline=1826;md5=a897a42bafc3422cab17c2eb94f87a7c"

SRC_URI = "${CPAN_MIRROR}/authors/id/R/RJ/RJBS/Test-Deep-${PV}.tar.gz"
SRC_URI[md5sum] = "d466e471108f7f7a5df3802cb13761ac"
SRC_URI[sha256sum] = "4064f494f5f62587d0ae501ca439105821ee5846c687dc6503233f55300a7c56"

UPSTREAM_CHECK_REGEX = "Test\-Deep\-(?P<pver>(\d+\.\d+))(?!_\d+).tar"

S = "${WORKDIR}/Test-Deep-${PV}"

inherit cpan ptest-perl

RDEPENDS_${PN} += " \
    perl-module-dynaloader \
    perl-module-exporter \
    perl-module-fcntl \
    perl-module-list-util \
    perl-module-scalar-util \
    perl-module-strict \
    perl-module-vars \
    perl-module-warnings \
"

RDEPENDS_${PN}-ptest += " \
    perl-module-if \
    perl-module-lib \
    perl-module-test-more \
    perl-module-test-tester \
"

BBCLASSEXTEND = "native"
