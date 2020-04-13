# Copyright (C) 2020 Jens Rehsack <sno@netbsd.org>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "A module to implement some of AutoConf macros in pure perl"
DESCRIPTION = "Config::AutoConf is intended to provide the same opportunities to Perl \
developers as GNU Autoconf <http://www.gnu.org/software/autoconf/> does for \
Shell developers."

HOMEPAGE=       "https://metacpan.org/release/Config-AutoConf"

SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0+"

LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Artistic-1.0;md5=cda03bbdc3c1951996392b872397b798 \
file://${COMMON_LICENSE_DIR}/GPL-1.0;md5=e9e36a9de734199567a4d769498f743d"

SRC_URI = "${CPAN_MIRROR}/authors/id/R/RE/REHSACK/Config-AutoConf-${PV}.tar.gz"

SRC_URI[md5sum] = "0bea721ad3996e20324b84b3b924ec71"
SRC_URI[sha256sum] = "33c930feec3003de251ca222abe8bbeb74610ad07f65fc16f27d74d195eeab34"
RDEPENDS_${PN} += "libcapture-tiny-perl"
RDEPENDS_${PN} += "perl-module-base"
RDEPENDS_${PN} += "perl-module-carp"
RDEPENDS_${PN} += "perl-module-config"
RDEPENDS_${PN} += "perl-module-constant"
RDEPENDS_${PN} += "perl-module-file-basename"
RDEPENDS_${PN} += "perl-module-file-spec"
RDEPENDS_${PN} += "perl-module-file-temp"
RDEPENDS_${PN} += "perl-module-extutils-cbuilder"
RDEPENDS_${PN} += "perl-module-extutils-cbuilder-platform-unix"
RDEPENDS_${PN} += "perl-module-scalar-util"
RDEPENDS_${PN} += "perl-module-strict"
RDEPENDS_${PN} += "perl-module-text-parsewords"
RDEPENDS_${PN} += "perl-module-warnings"
RRECOMMENDS_${PN} += "libfile-slurper-perl"

S = "${WORKDIR}/Config-AutoConf-${PV}"

inherit cpan ptest-perl

BBCLASSEXTEND = "native nativesdk"
