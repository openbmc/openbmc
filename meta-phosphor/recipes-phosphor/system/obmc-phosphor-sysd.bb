SUMMARY = "Phosphor OpenBMC System Management"
DESCRIPTION = "Phosphor OpenBMC system management reference implementation."
PR = "r1"

inherit obmc-phosphor-pydbus-service

PROVIDES += "virtual/obmc-system-mgmt"
RPROVIDES_${PN} += "virtual-obmc-system-mgmt"

S = "${WORKDIR}"
SRC_URI += "file://${PN}.py"
