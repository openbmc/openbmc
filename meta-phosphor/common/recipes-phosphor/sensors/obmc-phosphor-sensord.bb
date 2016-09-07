SUMMARY = "Phosphor OpenBMC Sensor Management"
DESCRIPTION = "Phosphor OpenBMC sensor management reference implementation."
PR = "r1"

inherit obmc-phosphor-pydbus-service

PROVIDES += "virtual/obmc-sensor-mgmt"
RPROVIDES_${PN} += "virtual-obmc-sensor-mgmt"

S = "${WORKDIR}"
SRC_URI += "file://${PN}.py"
