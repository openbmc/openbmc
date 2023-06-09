DESCRIPTION = "Trusted Services secure storage service provider"

require ts-sp-common.inc

SP_UUID = "${STORAGE_UUID}"
TS_SP_PS_CONFIG ?= "default"

OECMAKE_SOURCEPATH="${S}/deployments/protected-storage/config/${TS_SP_PS_CONFIG}-${TS_ENV}"
