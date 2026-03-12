DESCRIPTION = "Trusted Services Firmware Trusted Platform Module (fTPM) service provider"

require ts-sp-common.inc
require ts-ms-tpm20-ref_git.inc

SP_UUID = "${TS_FTPM_UUID}"
TS_SP_FTPM_CONFIG ?= "default"

OECMAKE_SOURCEPATH = "${S}/deployments/ftpm/config/${TS_SP_FTPM_CONFIG}-${TS_ENV}"
