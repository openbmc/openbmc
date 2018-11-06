SUMMARY = "Phosphor OpenBMC Fan Management."
DESCRIPTION = "Phosphor OpenBMC fan management reference implementation."
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${PHOSPHORBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

inherit pkgconfig
inherit obmc-phosphor-sdbus-service
inherit obmc-phosphor-c-daemon

PROVIDES += "virtual/obmc-fan-mgmt"
RPROVIDES_${PN} += "virtual-obmc-fan-mgmt"

S = "${WORKDIR}"
SRC_URI += "file://Makefile \
           file://obmc-phosphor-fand.c \
           "
