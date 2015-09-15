DEFAULTTUNE ?= "cortexm3"

require conf/machine/include/arm/arch-armv7a.inc

TUNEVALID[cortexm3] = "Enable Cortex-M3 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains("TUNE_FEATURES", "cortexm3", " -mtune=cortex-m3", "", d)}"

TUNEVALID[armv7m] = "Enable Cortex-M3 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains("TUNE_FEATURES", "armv7m", " -march=armv7-m", "", d)}"

AVAILTUNES += "cortexm3"
ARMPKGARCH_tune-cortexm3 = "armv7m"
TUNE_FEATURES_tune-cortexm3 = "armv7m vfp cortexm3"
PACKAGE_EXTRA_ARCHS_tune-cortexm3 = "${PACKAGE_EXTRA_ARCHS_tune-armv7at} armv7m-vfp"
