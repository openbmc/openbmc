SUMMARY = "Make sure you didn't emit any warnings while testing"
DESCRIPTION = "In general, your tests shouldn't produce warnings. This \
modules causes any warnings to be captured and stored. It automatically \
adds an extra test that will run when your script ends to check that there \
were no warnings. If there were any warings, the test will give a \\"not ok\\" \
and diagnostics of where, when and what the warning was, including a stack \
trace of what was going on when the it occurred.\
\
If some of your tests are supposed to produce warnings then you should \
be capturing and checking them with Test::Warn, that way \
Test::NoWarnings will not see them and so not complain.\
\
The test is run by an \\"END\\" block in Test::NoWarnings. It will not be \
run when any forked children exit."

SECTION = "libs"
LICENSE = "LGPL-2.1-only"

HOMEPAGE = "https://metacpan.org/release/ADAMK/Test-NoWarnings-1.04"

LIC_FILES_CHKSUM = "file://LICENSE;md5=7880c36af69ec5fb59c117cd41e6c38d"

CPAN_PACKAGE = "Test-NoWarnings"
CPAN_AUTHOR = "HAARG"

SRC_URI = "${CPAN_MIRROR}/authors/id/H/HA/${CPAN_AUTHOR}/${CPAN_PACKAGE}-${PV}.tar.gz"

SRC_URI[sha256sum] = "c2dc51143b7eb63231210e27df20d2c8393772e0a333547ec8b7a205ed62f737"

RDEPENDS:${PN} += "perl-module-test-builder perl-module-test-more perl-module-test-tester"

S = "${UNPACKDIR}/${CPAN_PACKAGE}-${PV}"

inherit cpan ptest-perl

BBCLASSEXTEND = "native nativesdk"
