DEFAULTTUNE ?= "armv4t"

require conf/machine/include/arm/arch-armv4.inc

TUNEVALID[arm9tdmi] = "Enable arm9tdmi specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains("TUNE_FEATURES", "arm9tdmi", " -mtune=arm9tdmi", "", d)}"

AVAILTUNES += "arm9tdmi"
ARMPKGARCH_tune-arm9tdmi = "arm9tdmi"
TUNE_FEATURES_tune-arm9tdmi = "${TUNE_FEATURES_tune-armv4t} arm9tdmi"
PACKAGE_EXTRA_ARCHS_tune-arm9tdmi = "${PACKAGE_EXTRA_ARCHS_tune-armv4t} arm9tdmit"
