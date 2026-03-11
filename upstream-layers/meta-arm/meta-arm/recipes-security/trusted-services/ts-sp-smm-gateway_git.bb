DESCRIPTION = "Trusted Services service provider for UEFI SMM services"

require ts-sp-common.inc

SP_UUID = "${SMM_GATEWAY_UUID}"
TS_SP_SMM_GATEWAY_CONFIG ?= "default"

OECMAKE_SOURCEPATH = "${S}/deployments/smm-gateway/config/${TS_SP_SMM_GATEWAY_CONFIG}-${TS_ENV}"

EXTRA_OECMAKE:append = "${@oe.utils.vartrue("SMMGW_AUTH_VAR", " -DUEFI_AUTH_VAR=ON ", "", d)}"
EXTRA_OECMAKE:append = "${@oe.utils.ifelse(oe.types.boolean(d.getVar("SMMGW_AUTH_VAR")) and oe.types.boolean(d.getVar("SMMGW_INTERNAL_CRYPTO")), " -DUEFI_INTERNAL_CRYPTO=On ", "")}"
