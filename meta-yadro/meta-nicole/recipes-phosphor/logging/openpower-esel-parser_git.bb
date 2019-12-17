SUMMARY = "OpenPOWER eSEL parser"
DESCRIPTION = "Shared library and console utility for parsing eSEL."
HOMEPAGE = "https://github.com/YADRO-KNS/openpower-esel-parser"
PR = "r1"
PV = "1.0+git${SRCPV}"

inherit autotools
inherit pkgconfig
inherit perlnative

# License info
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

# Dependencies
DEPENDS += "autoconf-archive-native \
            libxml-libxml-perl-native \
            libxml-simple-perl-native"

# Source code repository
S = "${WORKDIR}/git"
SRC_URI = "gitsm://github.com/YADRO-KNS/openpower-esel-parser"
SRCREV = "c35879fa605f3aa8098fff2c0a395815d8cbfe51"
