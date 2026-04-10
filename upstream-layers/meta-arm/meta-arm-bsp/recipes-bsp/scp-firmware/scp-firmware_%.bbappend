# Include machine specific SCP configurations

MACHINE_SCP_REQUIRE ?= ""

MACHINE_SCP_REQUIRE:juno = "scp-firmware-juno.inc"
MACHINE_SCP_REQUIRE:rdn2 = "scp-firmware-rdn2.inc"
MACHINE_SCP_REQUIRE:rdv2 = "scp-firmware-rdv2.inc"

require ${MACHINE_SCP_REQUIRE}
