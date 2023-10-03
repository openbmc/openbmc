DESCRIPTION = "Trusted Services attestation service provider"

require ts-sp-common.inc

SP_UUID = "${ATTESTATION_UUID}"
TS_SP_IAT_CONFIG ?= "default"

OECMAKE_SOURCEPATH="${S}/deployments/attestation/config/${TS_SP_IAT_CONFIG}-${TS_ENV}"
