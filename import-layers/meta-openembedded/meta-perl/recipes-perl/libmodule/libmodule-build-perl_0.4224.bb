SUMMARY = "Module::Build - Build and install Perl modules"
DESCRIPTION = "Many Perl distributions use a Build.PL file instead of a \
Makefile.PL file to drive distribution configuration, build, test and \
installation. Traditionally, Build.PL uses Module::Build as the underlying \
build system. This module provides a simple, lightweight, drop-in replacement. \
Whereas Module::Build has over 6,700 lines of code; this module has less than \
120, yet supports the features needed by most distributions."

SECTION = "libs"

HOMEPAGE = "https://metacpan.org/release/Module-Build"

LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = "file://README;beginline=960;endline=965;md5=624c06db56a2af4d70cf9edc29fcae1b"

SRC_URI = "${CPAN_MIRROR}/authors/id/L/LE/LEONT/Module-Build-${PV}.tar.gz"
SRC_URI[md5sum] = "b74c2f6e84b60aad3a3defd30b6f0f4d"
SRC_URI[sha256sum] = "a6ca15d78244a7b50fdbf27f85c85f4035aa799ce7dd018a0d98b358ef7bc782"

S = "${WORKDIR}/Module-Build-${PV}"

inherit cpan_build

# From:
# https://github.com/rehsack/meta-cpan/blob/master/recipes-devel/module-build-perl/module-build-perl_0.4216.bb
#
do_patch_module_build () {
    cd ${S}
    sed -i -e 's,my $interpreter = $self->{properties}{perl};,my $interpreter = "${bindir}/perl";,g' lib/Module/Build/Base.pm
}

do_patch[postfuncs] += "do_patch_module_build"

BBCLASSEXTEND = "native"
