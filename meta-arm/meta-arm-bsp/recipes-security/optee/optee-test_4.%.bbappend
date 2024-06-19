# Machine specific configurations

MACHINE_OPTEE_TEST_REQUIRE ?= ""
MACHINE_OPTEE_TEST_REQUIRE:fvp-base = "optee-test-fvp-base.inc"

require ${MACHINE_OPTEE_TEST_REQUIRE}
