#
# Tune Settings for Cortex-M1
#
DEFAULTTUNE ?= "cortexm1"

TUNEVALID[cortexm1] = "Enable Cortex-M1 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'cortexm1', ' -mcpu=cortex-m1', '', d)}"

require conf/machine/include/arm/arch-armv6m.inc

AVAILTUNES                            += "cortexm1"
ARMPKGARCH:tune-cortexm1               = "cortexm1"
TUNE_FEATURES:tune-cortexm1            = "${TUNE_FEATURES:tune-armv6m} cortexm1"
PACKAGE_EXTRA_ARCHS:tune-cortexm1      = "${PACKAGE_EXTRA_ARCHS:tune-armv6m} cortexm1"
