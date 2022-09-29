DESCRIPTION = "Trusted Services service provider for UEFI SMM services"

require ts-sp-common.inc

SP_UUID = "${SMM_GATEWAY_UUID}"

OECMAKE_SOURCEPATH="${S}/deployments/smm-gateway/${TS_ENV}"
