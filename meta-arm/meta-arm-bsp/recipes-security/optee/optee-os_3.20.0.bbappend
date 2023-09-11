# Machine specific configurations

MACHINE_OPTEE_OS_REQUIRE ?= ""
MACHINE_OPTEE_OS_REQUIRE:n1sdp = "optee-os-n1sdp.inc"
MACHINE_OPTEE_OS_REQUIRE:tc = "optee-os-tc.inc"

require ${MACHINE_OPTEE_OS_REQUIRE}
