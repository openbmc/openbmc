SUMMARY = "Crypt::URandom - Provide non blocking randomness"
DESCRIPTION = "Crypt::URandom reads random bytes from the getrandom(2) system \
call or /dev/urandom, providing cryptographically secure randomness that does \
not block."
HOMEPAGE = "https://metacpan.org/dist/Crypt-URandom"
SECTION = "libs"
LICENSE = "Artistic-1.0 OR GPL-1.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ec17831373365f90a71812362a07229a"

SRC_URI = "${CPAN_MIRROR}/authors/id/D/DD/DDICK/Crypt-URandom-${PV}.tar.gz"
SRC_URI[sha256sum] = "ef9f44141073c13573e85b148ff9a9089c45825b7d6608d832e4263899d3a2d4"

S = "${UNPACKDIR}/Crypt-URandom-${PV}"

inherit cpan ptest-perl

RDEPENDS:${PN} += "\
    perl-module-carp \
    perl-module-constant \
    perl-module-english \
    perl-module-exporter \
    perl-module-filehandle \
    perl-module-xsloader \
"

RDEPENDS:${PN}-ptest += "\
    perl-module-config \
    perl-module-encode \
    perl-module-overload \
    perl-module-posix \
    perl-module-test-more \
"

BBCLASSEXTEND = "native"
