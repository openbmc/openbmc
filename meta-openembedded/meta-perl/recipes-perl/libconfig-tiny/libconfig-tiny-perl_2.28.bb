SUMMARY = "Perl module for read/write .ini style files"
DESCRIPTION = "Config::Tiny is a Perl class to read and write .ini \
configuration files with as little code as possible, reducing load time and \
memory overhead."

HOMEPAGE = "https://search.cpan.org/dist/Config-Tiny"
SECTION = "libraries"

LICENSE = "Artistic-1.0 | GPL-1.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ab27c3cedbdb0eb6e656a8722476191a"

RDEPENDS:${PN} += "perl"

S = "${WORKDIR}/Config-Tiny-${PV}"

SRC_URI = "http://search.cpan.org/CPAN/authors/id/R/RS/RSAVAGE/Config-Tiny-${PV}.tgz"

SRC_URI[sha256sum] = "12df843a0d29d48f61bcc14c4f18f0858fd27a8dd829a00319529d654fe01500"

inherit cpan
