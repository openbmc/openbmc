# Machine specific configurations

MACHINE_OPTEE_TEST_REQUIRE ?= ""
MACHINE_OPTEE_TEST_REQUIRE:tc = "optee-test-tc.inc"

require ${MACHINE_OPTEE_TEST_REQUIRE}
