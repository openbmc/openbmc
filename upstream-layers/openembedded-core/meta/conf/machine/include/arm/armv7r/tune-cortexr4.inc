#
# Tune Settings for Cortex-R4
#
DEFAULTTUNE ?= "cortexr4"

TUNEVALID[cortexr4] = "Enable Cortex-R4 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'cortexr4', ' -mcpu=cortex-r4', '', d)}"

require conf/machine/include/arm/arch-armv7r.inc

AVAILTUNES                            += "cortexr4"
ARMPKGARCH:tune-cortexr4               = "cortexr4"
TUNE_FEATURES:tune-cortexr4            = "${TUNE_FEATURES:tune-armv7r} cortexr4"
PACKAGE_EXTRA_ARCHS:tune-cortexr4      = "${PACKAGE_EXTRA_ARCHS:tune-armv7r} cortexr4"
