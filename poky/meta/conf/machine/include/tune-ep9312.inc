DEFAULTTUNE ?= "ep9312"

require conf/machine/include/arm/arch-armv4.inc

TUNEVALID[ep9312] = "Enable Cirrus Logic EP9312 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'ep9312', ' -march=ep9312 -mcpu=ep9312', '', d)}"
MACHINEOVERRIDES =. "${@bb.utils.contains('TUNE_FEATURES', 'ep9312', 'armv4:', '' ,d)}"

AVAILTUNES += "ep9312"
ARMPKGARCH_tune-ep9312 = "ep9312"
# this tune does not include TUNE_FEATURES_tune-armv4t, so there is no armv4 TUNE_FEATURES => no 't' in ARMPKGSFX_THUMB
TUNE_FEATURES_tune-ep9312 = "thumb ep9312"
PACKAGE_EXTRA_ARCHS_tune-ep9312 = "${PACKAGE_EXTRA_ARCHS_tune-armv4t} ep9312"
