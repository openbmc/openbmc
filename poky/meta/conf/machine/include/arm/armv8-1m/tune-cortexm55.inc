#
# Tune Settings for Cortex-M55
#
DEFAULTTUNE ?= "cortexm55"

TUNEVALID[cortexm55] = "Enable Cortex-M55 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'cortexm55', ' -mcpu=cortex-m55', '', d)}"

require conf/machine/include/arm/arch-armv8-1m-main.inc

AVAILTUNES                            += "cortexm55"
ARMPKGARCH:tune-cortexm55              = "cortexm55"
# We do not want -march since -mcpu is added above to cover for it
TUNE_FEATURES:tune-cortexm55           = "cortexm55"
PACKAGE_EXTRA_ARCHS:tune-cortexm55     = "${PACKAGE_EXTRA_ARCHS:tune-armv8-1m-main} cortexm55"
