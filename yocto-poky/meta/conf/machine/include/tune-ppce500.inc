DEFAULTTUNE ?= "ppce500"

require conf/machine/include/powerpc/arch-powerpc.inc

TUNEVALID[ppce500] = "Enable ppce500 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains("TUNE_FEATURES", "ppce500", " -mcpu=8540", "", d)}"

TUNEVALID[spe] = "Enable SPE ABI extensions"
TUNE_CCARGS .= "${@bb.utils.contains("TUNE_FEATURES", [ "ppce500", "spe" ], " -mabi=spe -mspe -mfloat-gprs=single", "", d)}"
TARGET_FPU .= "${@bb.utils.contains("TUNE_FEATURES", [ "ppce500" , "spe" ], "ppc-efs", "", d)}"

# spe is defined potentially in two places, so we want to be sure it will
# only write spe once to the ABIEXTENSIONS field.
SPEABIEXTENSION = "${@bb.utils.contains("TUNE_FEATURES", "spe", "spe", "", d)}"
ABIEXTENSION .= "${SPEABIEXTENSION}"

AVAILTUNES += "ppce500"
TUNE_FEATURES_tune-ppce500 = "m32 spe ppce500"
TUNE_PKGARCH_tune-ppce500 = "ppce500"
PACKAGE_EXTRA_ARCHS_tune-ppce500 = "ppce500"
