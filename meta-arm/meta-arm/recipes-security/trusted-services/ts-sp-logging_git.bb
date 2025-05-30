DESCRIPTION = "Trusted Services logging service provider"

require ts-sp-common.inc

SP_UUID = "${LOGGING_SP_UUID}"
TS_SP_LOGGING_CONFIG ?= "default"

OECMAKE_SOURCEPATH = "${S}/deployments/logging/config/${TS_SP_LOGGING_CONFIG}-${TS_ENV}"
