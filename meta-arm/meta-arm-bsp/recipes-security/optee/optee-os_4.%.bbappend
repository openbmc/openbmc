
# Machine specific configurations
MACHINE_OPTEE_OS_REQUIRE ?= ""
MACHINE_OPTEE_OS_REQUIRE:corstone1000 = "optee-os-corstone1000-common.inc"
MACHINE_OPTEE_OS_REQUIRE:n1sdp = "optee-os-n1sdp.inc"

require ${MACHINE_OPTEE_OS_REQUIRE}
