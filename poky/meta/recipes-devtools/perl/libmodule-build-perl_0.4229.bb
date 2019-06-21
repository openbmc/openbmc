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
LIC_FILES_CHKSUM = "file://README;beginline=949;endline=954;md5=624c06db56a2af4d70cf9edc29fcae1b"

SRC_URI = "${CPAN_MIRROR}/authors/id/L/LE/LEONT/Module-Build-${PV}.tar.gz \
           file://run-ptest \
          "
SRC_URI[md5sum] = "1447d9e02e63f7a1643986789a8f1ba9"
SRC_URI[sha256sum] = "1fe491a6cda914b01bc8e592faa2b5404e9f35915ca15322f8f2a8d8f9008c18"

S = "${WORKDIR}/Module-Build-${PV}"

inherit cpan_build ptest-perl

# From:
# https://github.com/rehsack/meta-cpan/blob/master/recipes-devel/module-build-perl/module-build-perl_0.4216.bb
#
do_patch_module_build () {
    cd ${S}
    sed -i -e 's,my $interpreter = $self->{properties}{perl};,my $interpreter = "${bindir}/perl";,g' lib/Module/Build/Base.pm
}

do_patch[postfuncs] += "do_patch_module_build"

do_install_ptest() {
	cp -r ${B}/inc ${D}${PTEST_PATH}
	cp -r ${B}/blib ${D}${PTEST_PATH}
	chown -R root:root ${D}${PTEST_PATH}
}

RDEPENDS_${PN} += " \
    perl-module-carp \
    perl-module-cpan \
    perl-module-config \
    perl-module-cwd \
    perl-module-data-dumper \
    perl-module-encode \
    perl-module-extutils-cbuilder \
    perl-module-extutils-command \
    perl-module-extutils-install \
    perl-module-extutils-installed \
    perl-module-extutils-mkbootstrap \
    perl-module-extutils-packlist \
    perl-module-extutils-parsexs \
    perl-module-file-basename \
    perl-module-file-compare \
    perl-module-file-copy \
    perl-module-file-find \
    perl-module-file-glob \
    perl-module-file-path \
    perl-module-file-spec \
    perl-module-file-spec-functions \
    perl-module-getopt-long \
    perl-module-metadata \
    perl-module-perl-ostype \
    perl-module-pod-man \
    perl-module-tap-harness \
    perl-module-text-abbrev \
    perl-module-text-parsewords \
    perl-module-utf8 \
"

RDEPENDS_${PN}-ptest += " \
    gcc \
    make \
    perl-module-blib \
    perl-module-file-temp \
    perl-module-lib \
    perl-module-perlio \
    perl-module-perlio-encoding \
    perl-module-pod-text \
    perl-module-tap-harness-env \
    perl-module-tap-parser \
    perl-module-tap-parser-scheduler \
    perl-module-test-harness \
    perl-module-test-more \
"

RPROVIDES_${PN} += "\
    libmodule-build-base-perl \
    libmodule-build-compat-perl \
    libmodule-build-config-perl \
    libmodule-build-cookbook-perl \
    libmodule-build-dumper-perl \
    libmodule-build-notes-perl \
    libmodule-build-ppmaker-perl \
    libmodule-build-platform-default-perl \
    libmodule-build-platform-unix-perl \
    libmodule-build-podparser-perl \
"

BBCLASSEXTEND = "native"
