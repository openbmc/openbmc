SUMMARY = "OpenBMC YADRO list sensors tool"
DESCRIPTION = "The command line tool to show detailed information about \
               all available sensors"
HOMEPAGE = "https://github.com/YADRO-KNS/obmc-yadro-lssensors"
PR = "r1"
PV = "1.0+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

inherit meson

DEPENDS += "sdbusplus"

S = "${WORKDIR}/git"
SRC_URI = "git://github.com/YADRO-KNS/obmc-yadro-lssensors.git"
SRCREV  = "f76bf429d212d803cff7e4f1e3ca21097c65f18f"
