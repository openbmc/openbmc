#
# Tune Settings for Cortex-M3
#
DEFAULTTUNE ?= "cortexm3"

TUNEVALID[cortexm3] = "Enable Cortex-M3 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'cortexm3', ' -mcpu=cortex-m3', '', d)}"

require conf/machine/include/arm/arch-armv7m.inc

AVAILTUNES                            += "cortexm3"
ARMPKGARCH:tune-cortexm3               = "cortexm3"
TUNE_FEATURES:tune-cortexm3            = "${TUNE_FEATURES:tune-armv7m} cortexm3"
PACKAGE_EXTRA_ARCHS:tune-cortexm3      = "${PACKAGE_EXTRA_ARCHS:tune-armv7m} cortexm3"
