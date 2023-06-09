DESCRIPTION = "Trusted Services crypto service provider"

require ts-sp-common.inc

SP_UUID = "${CRYPTO_UUID}"
TS_SP_CRYPTO_CONFIG ?= "default"

DEPENDS += "python3-protobuf-native python3-jsonschema-native python3-jinja2-native"

OECMAKE_SOURCEPATH="${S}/deployments/crypto/config/${TS_SP_CRYPTO_CONFIG}-${TS_ENV}"
