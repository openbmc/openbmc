# Include machine specific SCP configurations

MACHINE_SCP_REQUIRE ?= ""

MACHINE_SCP_REQUIRE:juno = "scp-firmware-juno.inc"
MACHINE_SCP_REQUIRE:sgi575 = "scp-firmware-sgi575.inc"

require ${MACHINE_SCP_REQUIRE}
