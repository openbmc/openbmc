DESCRIPTION = "Trusted Services crypto service provider"

require ts-sp-common.inc

SP_UUID = "${CRYPTO_UUID}"

DEPENDS += "python3-protobuf-native"

OECMAKE_SOURCEPATH="${S}/deployments/crypto/${TS_ENV}"
