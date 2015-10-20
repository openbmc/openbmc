SUMMARY = "Phosphor OpenBMC Chassis Management"
DESCRIPTION = "Phosphor OpenBMC chassis management reference implementation."
PR = "r1"

inherit obmc-phosphor-chassis-mgmt
inherit obmc-phosphor-pydbus-service

S = "${WORKDIR}"
SRC_URI += "file://${PN}.py"
