SUMMARY = "Module::Build::Tiny - A tiny replacement for Module::Build"
DESCRIPTION = "Many Perl distributions use a Build.PL file instead of a \
Makefile.PL file to drive distribution configuration, build, test and \
installation. Traditionally, Build.PL uses Module::Build as the underlying \
build system. This module provides a simple, lightweight, drop-in replacement. \
Whereas Module::Build has over 6,700 lines of code; this module has less than \
120, yet supports the features needed by most distributions."
SECTION = "libs"

HOMEPAGE = "http://search.cpan.org/~kwilliams/Module-Build-0.31/"

LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = "file://LICENSE;md5=43339d8f9d3a956ee9eceb07717ee95e"

SRC_URI = "${CPAN_MIRROR}/authors/id/K/KW/KWILLIAMS/Module-Build-${PV}.tar.gz"
SRC_URI[md5sum] = "3d4fdffe58f6236253767e5a71edf29b"
SRC_URI[sha256sum] = "e2f723be8d6c70b4ddbca3b5e32e52e6e98eae8f43e34d7ede87efcb1796bbb5"

S = "${WORKDIR}/Module-Build-${PV}"

inherit cpan_build

do_install () {
        cpan_build_do_install
}

BBCLASSEXTEND = "native"
