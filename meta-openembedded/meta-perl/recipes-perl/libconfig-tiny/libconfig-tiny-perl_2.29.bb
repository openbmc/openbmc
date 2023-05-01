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

SRC_URI[sha256sum] = "3de79b0ea03a8d6a93e9d9128fe845fb556222b14699a4f6f0d5ca057ae3333b"

inherit cpan
