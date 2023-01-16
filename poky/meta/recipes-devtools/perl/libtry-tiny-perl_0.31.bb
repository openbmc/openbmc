SUMMARY = "Try::Tiny - Minimal try/catch with proper preservation of $@"
DESCRIPTION = "This module provides bare bones try/catch/finally statements \
that are designed to minimize common mistakes with eval blocks, and NOTHING \
else."
HOMEPAGE = "https://github.com/p5sagit/Try-Tiny"
BUGTRACKER = "https://rt.cpan.org/Public/Dist/Display.html?Name=Try-Tiny"
SECTION = "libs"
LICENSE = "MIT"

LIC_FILES_CHKSUM = "file://LICENCE;md5=5dc332c2d4aade55f5db244681000091"

SRC_URI = "${CPAN_MIRROR}/authors/id/E/ET/ETHER/Try-Tiny-${PV}.tar.gz"

SRC_URI[sha256sum] = "3300d31d8a4075b26d8f46ce864a1d913e0e8467ceeba6655d5d2b2e206c11be"

S = "${WORKDIR}/Try-Tiny-${PV}"

inherit cpan ptest-perl

RDEPENDS:${PN} += "\
    perl-module-carp \
    perl-module-constant \
    perl-module-exporter \
"
RRECOMMENDS:${PN} += "\
    perl-module-sub-util \
"
RDEPENDS:${PN}-ptest += "\
    perl-module-extutils-makemaker \
    perl-module-extutils-mm-unix \
    perl-module-file-spec \
    perl-module-if \
    perl-module-test-more \
"

BBCLASSEXTEND = "native nativesdk"
