# Copyright (C) 2020 Jens Rehsack <sno@netbsd.org>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "A module to implement some of AutoConf macros in pure perl"
DESCRIPTION = "Config::AutoConf is intended to provide the same opportunities to Perl \
developers as GNU Autoconf <http://www.gnu.org/software/autoconf/> does for \
Shell developers."

HOMEPAGE=       "https://metacpan.org/release/Config-AutoConf"

SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0-or-later"

LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Artistic-1.0;md5=cda03bbdc3c1951996392b872397b798 \
file://${COMMON_LICENSE_DIR}/GPL-1.0-or-later;md5=30c0b8a5048cc2f4be5ff15ef0d8cf61"

SRC_URI = "${CPAN_MIRROR}/authors/id/R/RE/REHSACK/Config-AutoConf-${PV}.tar.gz"

SRC_URI[md5sum] = "eeeab8cc93eef7dd2e2c343ecdf247b7"
SRC_URI[sha256sum] = "304f66cc2653264c0fe127d21669e86d3d18cd72f2574d8f5131beec31a0a33e"
RDEPENDS:${PN} += "libcapture-tiny-perl"
RDEPENDS:${PN} += "perl-module-base"
RDEPENDS:${PN} += "perl-module-carp"
RDEPENDS:${PN} += "perl-module-config"
RDEPENDS:${PN} += "perl-module-constant"
RDEPENDS:${PN} += "perl-module-file-basename"
RDEPENDS:${PN} += "perl-module-file-spec"
RDEPENDS:${PN} += "perl-module-file-temp"
RDEPENDS:${PN} += "perl-module-extutils-cbuilder"
RDEPENDS:${PN} += "perl-module-extutils-cbuilder-platform-unix"
RDEPENDS:${PN} += "perl-module-scalar-util"
RDEPENDS:${PN} += "perl-module-strict"
RDEPENDS:${PN} += "perl-module-text-parsewords"
RDEPENDS:${PN} += "perl-module-warnings"
RRECOMMENDS:${PN} += "libfile-slurper-perl"

S = "${WORKDIR}/Config-AutoConf-${PV}"

inherit cpan ptest-perl

BBCLASSEXTEND = "native"
