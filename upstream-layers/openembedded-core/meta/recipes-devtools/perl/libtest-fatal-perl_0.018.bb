SUMMARY = "Incredibly simple helpers for testing code with exceptions"
DESCRIPTION = "Test::Fatal is an alternative to the popular Test::Exception.\
It does much less, but should allow greater flexibility in testing \
exception-throwing code with about the same amount of typing."
HOMEPAGE = "https://github.com/rjbs/Test-Fatal"
BUGTRACKER = "https://github.com/rjbs/Test-Fatal/issues"
SECTION = "libs"
LICENSE = "Artistic-1.0-Perl | GPL-1.0-or-later"

LIC_FILES_CHKSUM = "file://LICENSE;md5=f7fc7d4b26ddae0843b09ba56109e65a"

SRC_URI = "${CPAN_MIRROR}/authors/id/R/RJ/RJBS/Test-Fatal-${PV}.tar.gz"

SRC_URI[sha256sum] = "b8d2cccf9ee467271bc478f9cf7eba49545452be9302ae359bc538b8bf687cd6"

S = "${UNPACKDIR}/Test-Fatal-${PV}"

inherit cpan ptest-perl

RDEPENDS:${PN} += "\
    libtry-tiny-perl \
    perl-module-carp \
    perl-module-exporter \
    perl-module-test-builder \
"

RDEPENDS:${PN}-ptest += "\
    perl-module-extutils-makemaker \
    perl-module-extutils-mm-unix \
    perl-module-file-spec \
    perl-module-overload \
    perl-module-test-builder-tester \
    perl-module-test-more \
"

BBCLASSEXTEND = "native nativesdk"
