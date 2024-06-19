DESCRIPTION = "Trusted Services SPMC test SPs"

# spm test SP only supports opteesp.
TS_ENV = 'opteesp'

require ts-sp-common.inc

SP_UUID = "${SPM_TEST${SP_INDEX}_UUID}"
SP_DTS_FILE ?= "${D}${TS_INSTALL}/manifest/${SP_UUID}.dts"
OECMAKE_SOURCEPATH="${S}/deployments/spm-test${SP_INDEX}/${TS_ENV}"
