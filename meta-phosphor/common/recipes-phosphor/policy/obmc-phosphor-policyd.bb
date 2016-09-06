SUMMARY = "Phosphor OpenBMC Policy Management"
DESCRIPTION = "Phosphor OpenBMC policy management reference implementation."
PR = "r1"

inherit obmc-phosphor-policy-mgmt
inherit obmc-phosphor-pydbus-service

S = "${WORKDIR}"
SRC_URI += "file://${PN}.py"
