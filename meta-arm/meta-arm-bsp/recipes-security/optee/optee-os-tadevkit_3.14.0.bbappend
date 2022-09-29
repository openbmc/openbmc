# Machine specific configurations

MACHINE_OPTEE_OS_TADEVKIT_REQUIRE ?= ""
MACHINE_OPTEE_OS_TADEVKIT_REQUIRE:tc = "optee-os-generic-tc.inc"

require ${MACHINE_OPTEE_OS_TADEVKIT_REQUIRE}
