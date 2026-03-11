
# Machine specific configurations
MACHINE_OPTEE_OS_REQUIRE ?= ""
MACHINE_OPTEE_OS_REQUIRE:corstone1000 = "optee-os-corstone1000-common.inc"
MACHINE_OPTEE_OS_REQUIRE:fvp-base = "optee-os-fvp-base.inc"
MACHINE_OPTEE_OS_REQUIRE:sbsa-ref = "optee-os-sbsa-ref.inc"

require ${MACHINE_OPTEE_OS_REQUIRE}
