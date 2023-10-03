DESCRIPTION = "Trusted Services service provider for UEFI SMM services"

require ts-sp-common.inc

SP_UUID = "${SMM_GATEWAY_UUID}"
TS_SP_SMM_GATEWAY_CONFIG ?= "default"

OECMAKE_SOURCEPATH="${S}/deployments/smm-gateway/config/${TS_SP_SMM_GATEWAY_CONFIG}-${TS_ENV}"
