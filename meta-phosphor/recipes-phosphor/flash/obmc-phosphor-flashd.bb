SUMMARY = "Phosphor OpenBMC Flash Management"
DESCRIPTION = "Phosphor OpenBMC flash management reference implementation."
PR = "r1"

inherit obmc-phosphor-pydbus-service

PROVIDES += "virtual/obmc-flash-mgmt"
RPROVIDES_${PN} += "virtual-obmc-flash-mgmt"

S = "${WORKDIR}"
SRC_URI += "file://${PN}.py"
