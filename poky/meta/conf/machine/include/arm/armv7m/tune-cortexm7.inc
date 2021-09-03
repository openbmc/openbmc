#
# Tune Settings for Cortex-M7
#
DEFAULTTUNE ?= "cortexm7"

TUNEVALID[cortexm7] = "Enable Cortex-M7 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'cortexm7', ' -mcpu=cortex-m7', '', d)}"

require conf/machine/include/arm/arch-armv7em.inc

AVAILTUNES                            += "cortexm7"
ARMPKGARCH:tune-cortexm7               = "cortexm7"
TUNE_FEATURES:tune-cortexm7            = "${TUNE_FEATURES:tune-armv7em} cortexm7"
PACKAGE_EXTRA_ARCHS:tune-cortexm7      = "${PACKAGE_EXTRA_ARCHS:tune-armv7em} cortexm7"
