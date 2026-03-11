DESCRIPTION = "Internal Trusted Storage PSA certification tests (psa-arch-test) for Trusted Services"

TS_ENV = "arm-linux"

require ts-psa-api-test-common_${PV}.inc

OECMAKE_SOURCEPATH = "${S}/deployments/psa-api-test/internal_trusted_storage/${TS_ENV}"

PSA_TEST = "psa-its-api-test"
