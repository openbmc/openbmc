# Machine specific configurations

MACHINE_OPTEE_OS_REQUIRE ?= ""
MACHINE_OPTEE_OS_REQUIRE:corstone1000 = "optee-os_corstone1000.inc"
MACHINE_OPTEE_OS_REQUIRE:tc = "optee-os-tc.inc"

require ${MACHINE_OPTEE_OS_REQUIRE}
