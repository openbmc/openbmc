# Machine specific configurations

MACHINE_OPTEE_OS_TADEVKIT_REQUIRE ?= ""
MACHINE_OPTEE_OS_TADEVKIT_REQUIRE:n1sdp = "optee-os-n1sdp.inc"
MACHINE_OPTEE_OS_TADEVKIT_REQUIRE:fvp-base = "optee-os-fvp-base.inc"

require ${MACHINE_OPTEE_OS_TADEVKIT_REQUIRE}
