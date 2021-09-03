DEFAULTTUNE ?= "cortexm0"
require conf/machine/include/arm/arch-armv6m.inc

TUNEVALID[cortexm0] = "Enable Cortex-M0 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'cortexm0', ' -mcpu=cortex-m0', '', d)}"
AVAILTUNES += "cortexm0"

ARMPKGARCH:tune-cortexm0 = "cortexm0"
TUNE_FEATURES:tune-cortexm0 = "${TUNE_FEATURES:tune-armv6m} cortexm0"

PACKAGE_EXTRA_ARCHS:tune-cortexm0 = "${PACKAGE_EXTRA_ARCHS:tune-armv6m} cortexm0"
