DEFAULTTUNE ?= "cortexm0-plus"
require conf/machine/include/arm/arch-armv6m.inc

TUNEVALID[cortexm0-plus] = "Enable Cortex-M0 Plus specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'cortexm0-plus', ' -mcpu=cortex-m0plus', '', d)}"
AVAILTUNES += "cortexm0-plus"

ARMPKGARCH:tune-cortexm0-plus = "cortexm0-plus"
TUNE_FEATURES:tune-cortexm0-plus = "${TUNE_FEATURES:tune-armv6m} cortexm0-plus"

PACKAGE_EXTRA_ARCHS:tune-cortexm0-plus = "${PACKAGE_EXTRA_ARCHS:tune-armv6m} cortexm0-plus"
