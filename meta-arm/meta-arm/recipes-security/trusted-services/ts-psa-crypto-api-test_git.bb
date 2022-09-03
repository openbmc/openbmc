DESCRIPTION = "Crypto PSA certification tests (psa-arch-test)"

TS_ENV = "arm-linux"

require ts-psa-api-test-common_${PV}.inc

OECMAKE_SOURCEPATH = "${S}/deployments/psa-api-test/crypto/${TS_ENV}"

PSA_TEST = "psa-crypto-api-test"
