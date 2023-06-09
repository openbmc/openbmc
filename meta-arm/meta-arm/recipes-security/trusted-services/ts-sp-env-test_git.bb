DESCRIPTION = "Trusted Services test_runner service provider"

require ts-sp-common.inc

# Current version of env-test SP contains hard-coded values for FVP.
COMPATIBLE_MACHINE ?= "invalid"

SP_UUID = "${ENV_TEST_UUID}"
TS_SP_ENVTEST_CONFIG ?= "baremetal-fvp_base_revc"

OECMAKE_SOURCEPATH="${S}/deployments/env-test/config/${TS_SP_ENVTEST_CONFIG}-${TS_ENV}"
