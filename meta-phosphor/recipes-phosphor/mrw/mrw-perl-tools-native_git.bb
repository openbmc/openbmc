SUMMARY = "OpenBMC MRW Perl Tools"
DESCRIPTION = "OpenBMC Perl tools for the machine readable workbook"
PR = "r1"
PV = "1.0+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"
S = "${WORKDIR}/git"

inherit cpan_build
inherit mrw-rev
inherit native

DEPENDS += "libmodule-build-perl-native mrw-api-native yaml-tiny-native"

SRC_URI += "${MRW_TOOLS_SRC_URI}"
SRCREV = "${MRW_TOOLS_SRCREV}"
