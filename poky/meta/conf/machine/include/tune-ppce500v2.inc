DEFAULTTUNE ?= "ppce500v2"

require conf/machine/include/powerpc/arch-powerpc.inc

TUNEVALID[ppce500v2] = "Enable ppce500v2 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'ppce500v2', ' -mcpu=8548', '', d)}"

TUNEVALID[spe] = "Enable SPE ABI extensions"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', [ 'ppce500v2', 'spe' ], ' -mabi=spe -mspe -mfloat-gprs=double', '', d)}"
TARGET_FPU .= "${@bb.utils.contains('TUNE_FEATURES', [ 'ppce500v2' , 'spe' ], 'ppc-efd', '', d)}"

# spe is defined potentially in two places, so we want to be sure it will
# only write spe once to the ABIEXTENSIONS field.
SPEABIEXTENSION = "${@bb.utils.filter('TUNE_FEATURES', 'spe', d)}"
ABIEXTENSION .= "${SPEABIEXTENSION}"

AVAILTUNES += "ppce500v2"
TUNE_FEATURES_tune-ppce500v2 = "m32 ppce500v2 bigendian"
TUNE_PKGARCH_tune-ppce500v2 = "ppce500v2"
PACKAGE_EXTRA_ARCHS_tune-ppce500v2 = "ppce500v2"
