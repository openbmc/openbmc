DESCRIPTION = "Trusted Services proxy service providers"

require ts-sp-common.inc

SP_UUID = "${SE_PROXY_UUID}"

DEPENDS += "python3-protobuf-native"

OECMAKE_SOURCEPATH="${S}/deployments/se-proxy/${TS_ENV}"
