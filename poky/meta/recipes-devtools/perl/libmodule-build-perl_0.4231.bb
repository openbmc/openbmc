SUMMARY = "Module::Build - Build and install Perl modules"
DESCRIPTION = "Many Perl distributions use a Build.PL file instead of a \
Makefile.PL file to drive distribution configuration, build, test and \
installation. Traditionally, Build.PL uses Module::Build as the underlying \
build system. This module provides a simple, lightweight, drop-in replacement. \
Whereas Module::Build has over 6,700 lines of code; this module has less than \
120, yet supports the features needed by most distributions."

SECTION = "libs"

HOMEPAGE = "https://metacpan.org/release/Module-Build"

LICENSE = "Artistic-1.0 | GPL-1.0-or-later"
LIC_FILES_CHKSUM = "file://README;beginline=949;endline=954;md5=624c06db56a2af4d70cf9edc29fcae1b"

SRC_URI = "${CPAN_MIRROR}/authors/id/L/LE/LEONT/Module-Build-${PV}.tar.gz \
           file://run-ptest \
           "
SRC_URI[md5sum] = "066b193e461d7dfe1eca17a139353001"
SRC_URI[sha256sum] = "7e0f4c692c1740c1ac84ea14d7ea3d8bc798b2fb26c09877229e04f430b2b717"

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

EXTRA_CPAN_BUILD_FLAGS = "--create_packlist=0"

do_install:append () {
        rm -rf ${D}${docdir}/perl/html
        sed -i "s:^#!.*:#!/usr/bin/env perl:" ${D}${bindir}/config_data
}

do_install_ptest() {
	cp -r ${B}/inc ${D}${PTEST_PATH}
	cp -r ${B}/blib ${D}${PTEST_PATH}
	cp -r ${B}/_build ${D}${PTEST_PATH}
	cp -r ${B}/lib ${D}${PTEST_PATH}
	chown -R root:root ${D}${PTEST_PATH}
	sed -i -e "s,'perl' => .*,'perl' => '/usr/bin/perl'\,,g" \
               -e "s,${STAGING_BINDIR_NATIVE}/perl-native/\.\.,${bindir}/,g" \
               -e "s,${S},,g" \
               -e "s,${D},,g" \
               ${D}${PTEST_PATH}/_build/build_params \
               ${D}${PTEST_PATH}/_build/runtime_params
        rm -rf ${D}${PTEST_PATH}/blib/libhtml/site/lib/Module/
        rm -rf ${D}${PTEST_PATH}/_build/magicnum
}

RDEPENDS:${PN} += " \
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

RDEPENDS:${PN}-ptest += " \
    packagegroup-core-buildessential \
    perl-dev \
    perl-module-blib \
    perl-module-extutils-command-mm \
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

RPROVIDES:${PN} += "\
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

# t/xs.t RDEPENDS on "EXTERN.h" provided by perl-dev
INSANE_SKIP:${PN}-ptest = "dev-deps"

BBCLASSEXTEND = "native"
