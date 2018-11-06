SUMMARY = "Phosphor OpenBMC Flash Management"
DESCRIPTION = "Phosphor OpenBMC flash management reference implementation."
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${PHOSPHORBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

inherit obmc-phosphor-pydbus-service

PROVIDES += "virtual/obmc-flash-mgmt"
RPROVIDES_${PN} += "virtual-obmc-flash-mgmt"

S = "${WORKDIR}"
SRC_URI += "file://${PN}.py"
