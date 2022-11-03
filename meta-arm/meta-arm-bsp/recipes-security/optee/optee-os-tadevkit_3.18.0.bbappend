# Machine specific configurations

MACHINE_OPTEE_OS_TADEVKIT_REQUIRE ?= ""
MACHINE_OPTEE_OS_TADEVKIT_REQUIRE:tc = "optee-os-generic-tc.inc"
MACHINE_OPTEE_OS_TADEVKIT_REQUIRE:n1sdp = "optee-os-n1sdp.inc"

require ${MACHINE_OPTEE_OS_TADEVKIT_REQUIRE}
