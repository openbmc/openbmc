SUMMARY = "Module::Build::Tiny - A tiny replacement for Module::Build"
DESCRIPTION = "Many Perl distributions use a Build.PL file instead of a \
Makefile.PL file to drive distribution configuration, build, test and \
installation. Traditionally, Build.PL uses Module::Build as the underlying \
build system. This module provides a simple, lightweight, drop-in replacement. \
Whereas Module::Build has over 6,700 lines of code; this module has less than \
120, yet supports the features needed by most distributions."
SECTION = "libs"

HOMEPAGE = "http://search.cpan.org/~leont/Module-Build-Tiny/"

LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = "file://LICENSE;md5=aaca61412962cf972aec0cdad99d0a84"

DEPENDS = "libextutils-config-perl-native libextutils-helpers-perl-native libextutils-installpaths-perl-native"

SRC_URI = "${CPAN_MIRROR}/authors/id/L/LE/LEONT/Module-Build-Tiny-${PV}.tar.gz"
SRC_URI[md5sum] = "bfc92c655158ce623f0ced94f4ef02e5"
SRC_URI[sha256sum] = "d6706bf35e080e5af20cccf4fd565cc8af9c2a1e2e2075cee0a7de42cf0d6df9"

S = "${WORKDIR}/Module-Build-Tiny-${PV}"

inherit cpan_build

do_install () {
        cpan_build_do_install
}

RDEPENDS_${PN} = " libextutils-config-perl \
                   libextutils-helpers-perl \
                   libextutils-installpaths-perl \
                   perl-module-xsloader \
                   perl-module-file-spec \
                   perl-module-io-handle \
                   perl-module-tap-harness-env \
                   perl-module-ipc-open3 \
                   perl-module-file-path \
                   perl-module-cpan \
                   perl-module-extutils-cbuilder \
                   perl-module-getopt-long \
                   perl-module-extutils-makemaker \
                   perl-module-exporter \
                   perl-module-carp \
                   perl-module-test-more \
                   perl-module-text-parsewords \
                   perl-module-load \
                   perl-module-file-temp \
                   perl-module-data-dumper \
                   perl-module-extutils-parsexs \
                   perl-module-pod-man \
                   perl-module-json-pp \
"

BBCLASSEXTEND = "native"
