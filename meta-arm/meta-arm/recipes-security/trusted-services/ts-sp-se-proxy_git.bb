DESCRIPTION = "Trusted Services proxy service providers"

require ts-sp-common.inc

SP_UUID = "${SE_PROXY_UUID}"
TS_SP_SE_PROXY_CONFIG ?= "default"

DEPENDS += "python3-protobuf-native"

OECMAKE_SOURCEPATH = "${S}/deployments/se-proxy/config/${TS_SP_SE_PROXY_CONFIG}-${TS_ENV}"
