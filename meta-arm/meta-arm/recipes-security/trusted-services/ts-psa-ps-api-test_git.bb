DESCRIPTION = "Protected Storage PSA certification tests (psa-arch-test) for Trusted Services"

TS_ENV = "arm-linux"

require ts-psa-api-test-common_${PV}.inc

OECMAKE_SOURCEPATH = "${S}/deployments/psa-api-test/protected_storage/${TS_ENV}"

PSA_TEST = "psa-ps-api-test"
