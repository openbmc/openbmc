DESCRIPTION = "Trusted Services attestation service provider"

require ts-sp-common.inc

SP_UUID = "${ATTESTATION_UUID}"

OECMAKE_SOURCEPATH="${S}/deployments/attestation/${TS_ENV}"
