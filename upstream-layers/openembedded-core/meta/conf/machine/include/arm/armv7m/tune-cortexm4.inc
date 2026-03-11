#
# Tune Settings for Cortex-M4
#
DEFAULTTUNE ?= "cortexm4"

TUNEVALID[cortexm4] = "Enable Cortex-M4 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'cortexm4', ' -mcpu=cortex-m4', '', d)}"

require conf/machine/include/arm/arch-armv7em.inc

AVAILTUNES                            += "cortexm4"
ARMPKGARCH:tune-cortexm4               = "cortexm4"
TUNE_FEATURES:tune-cortexm4            = "${TUNE_FEATURES:tune-armv7em} cortexm4"
PACKAGE_EXTRA_ARCHS:tune-cortexm4      = "${PACKAGE_EXTRA_ARCHS:tune-armv7em} cortexm4"
