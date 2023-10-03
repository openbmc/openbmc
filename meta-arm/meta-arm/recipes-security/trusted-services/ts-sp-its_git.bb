DESCRIPTION = "Trusted Services internal secure storage service provider"

require ts-sp-common.inc

SP_UUID = "${ITS_UUID}"
TS_SP_ITS_CONFIG ?= "default"

OECMAKE_SOURCEPATH="${S}/deployments/internal-trusted-storage/config/${TS_SP_ITS_CONFIG}-${TS_ENV}"
