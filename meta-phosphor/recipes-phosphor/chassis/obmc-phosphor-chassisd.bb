SUMMARY = "Phosphor OpenBMC Chassis Management"
DESCRIPTION = "Phosphor OpenBMC chassis management reference implementation."
PR = "r1"

inherit obmc-phosphor-pydbus-service

PROVIDES += "virtual/obmc-chassis-mgmt"
RPROVIDES_${PN} += "virtual-obmc-chassis-mgmt"

S = "${WORKDIR}"
SRC_URI += "file://${PN}.py"
