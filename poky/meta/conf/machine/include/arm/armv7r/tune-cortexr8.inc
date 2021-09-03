#
# Tune Settings for Cortex-R8
#
DEFAULTTUNE ?= "cortexr8"

TUNEVALID[cortexr8] = "Enable Cortex-R8 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'cortexr8', ' -mcpu=cortex-r8', '', d)}"

require conf/machine/include/arm/arch-armv7r.inc

AVAILTUNES                            += "cortexr8"
ARMPKGARCH:tune-cortexr8               = "cortexr8"
TUNE_FEATURES:tune-cortexr8            = "${TUNE_FEATURES:tune-armv7r-vfpv3d16} cortexr8 idiv"
PACKAGE_EXTRA_ARCHS:tune-cortexr8      = "${PACKAGE_EXTRA_ARCHS:tune-armv7r-vfpv3d16} cortexr8-vfpv3d16"
