# Machine specific configurations

MACHINE_OPTEE_TEST_REQUIRE ?= ""
MACHINE_OPTEE_TEST_REQUIRE:tc = "optee-test-tc.inc"
MACHINE_OPTEE_TEST_REQUIRE:n1sdp = "optee-os-generic-n1sdp.inc"

require ${MACHINE_OPTEE_TEST_REQUIRE}
