HOMEPAGE =  "https://github.com/open-power/ipl/"

SUMMARY     = "Initial Program Load steps"
DESCRIPTION = "Provides infrastructure to run istep"
PR = "r1"
PV = "1.0+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

inherit autotools pkgconfig

S = "${WORKDIR}/git"

SRC_URI = "git://git@github.com/open-power/ipl;branch="main";protocol=https"
SRCREV = "1e1ec57270b52d0546b3998dbeacbdbd586a707b"

DEPENDS = " \
        libekb pdbg autoconf-archive guard pdata \
        "

RDEPENDS:${PN} = "phal-devtree"

EXTRA_OECONF = "CHIP=p10 --enable-libphal"
