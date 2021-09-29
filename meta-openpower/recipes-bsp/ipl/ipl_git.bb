HOMEPAGE =  "https://github.com/open-power/ipl/"

SUMMARY     = "Initial Program Load steps"
DESCRIPTION = "Provides infrastructure to run istep"
PR = "r1"
PV = "1.0+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

inherit autotools

S = "${WORKDIR}/git"

SRC_URI = "git://git@github.com/open-power/ipl;branch="main""
SRCREV = "2af23633e266a6513124fce995e07c121c5cf3df"

DEPENDS = " \
        libekb pdbg autoconf-archive guard \
        "

RDEPENDS:${PN} = "phal-devtree"

EXTRA_OECONF = "CHIP=p10"
