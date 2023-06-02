HOMEPAGE =  "https://github.com/open-power/libekb_p10"

SUMMARY     = "Hardware Procedure Framework"
DESCRIPTION = "Provides infrastructure to run hardware procedures"
PR = "r1"
PV = "1.0+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

S = "${WORKDIR}/git"

require recipes-bsp/ekb/ekb.inc

inherit autotools
inherit python3native
inherit perlnative

SRCREV_FORMAT = "libekb_ekb"

SRCREV_libekb = "3d1b2963082d7725c3a4a2960d6f3f3336f401a7"
SRCREV_ekb = "${EKB_REV}"

SRC_URI = "git://git@github.com/open-power/libekb_p10;branch=main;name=libekb;protocol=https \
           ${EKB_URI};name=ekb;destsuffix=git/ekb \
           "

DEPENDS = "pdbg libxml-simple-perl-native"
