HOMEPAGE =  "https://github.com/open-power/libekb_p10"

SUMMARY     = "Hardware Procedure Framework"
DESCRIPTION = "Provides infrastructure to run hardware procedures"
PR = "r1"
PV = "1.0+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

S = "${WORKDIR}/git"

require recipes-bsp/ekb/ekb.inc

inherit autotools \
        python3native \
        perlnative

SRCREV_FORMAT = "libekb_ekb"

SRCREV_libekb = "742ec3d7035be9396d324df597f48ab7d749f3dd"
SRCREV_ekb = "${EKB_REV}"

SRC_URI = "git://git@github.com/open-power/libekb_p10;branch="main";name=libekb \
           ${EKB_URI};name=ekb;destsuffix=git/ekb \
           "

DEPENDS = "pdbg libxml-simple-perl-native"
