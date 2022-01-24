SUMMARY = "OpenBMC YADRO list sensors tool"
DESCRIPTION = "The command line tool to show detailed information about \
               all available sensors"
HOMEPAGE = "https://github.com/YADRO-KNS/obmc-yadro-lssensors"
PR = "r1"
PV = "1.0+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

inherit pkgconfig meson

DEPENDS += "sdbusplus"

S = "${WORKDIR}/git"
SRC_URI = "git://github.com/YADRO-KNS/obmc-yadro-lssensors.git;branch=master;protocol=https"
SRCREV  = "dcaac17c62c9dd21499c4c5e9eb8070f10786560"
