SUMMARY = "Test::Warn - Perl extension to test methods for warnings"
DESCRIPTION = "This module provides a few convenience methods for testing \
warning based code. \
\
If you are not already familiar with the Test::More manpage now would be \
the time to go take a look. \
"

SECTION = "libs"
HOMEPAGE = "https://metacpan.org/release/Test-Warn"

LICENSE = "Artistic-1.0 | GPL-1.0-or-later"
LIC_FILES_CHKSUM = "file://README;beginline=73;endline=78;md5=42b423d91c92ba59c215835a2ee9b57a"

CPAN_PACKAGE = "Test-Warn"
CPAN_AUTHOR = "BIGJ"

SRC_URI = "${CPAN_MIRROR}/authors/id/B/BI/${CPAN_AUTHOR}/${CPAN_PACKAGE}-${PV}.tar.gz"

SRC_URI[sha256sum] = "98ca32e7f2f5ea89b8bfb9a0609977f3d153e242e2e51705126cb954f1a06b57"

S = "${WORKDIR}/${CPAN_PACKAGE}-${PV}"

inherit cpan ptest-perl

do_install_ptest() {
    cp -r ${B}/blib ${D}${PTEST_PATH}
    chown -R root:root ${D}${PTEST_PATH}
}

RDEPENDS:${PN} += " \
    libsub-uplevel-perl \
    perl-module-blib \
    perl-module-carp \
    perl-module-test-builder \
    perl-module-test-builder-tester \
    perl-module-test-tester \
"

RDEPENDS:${PN}-ptest += " \
    perl-module-file-spec \
    perl-module-test-more \
"

BBCLASSEXTEND = "native nativesdk"
