#
# Tune Settings for Cortex-R5
#
DEFAULTTUNE ?= "cortexr5"

TUNEVALID[cortexr5] = "Enable Cortex-R5 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'cortexr5', ' -mcpu=cortex-r5', '', d)}"

require conf/machine/include/arm/arch-armv7r.inc

AVAILTUNES                            += "cortexr5"
ARMPKGARCH:tune-cortexr5               = "cortexr5"
TUNE_FEATURES:tune-cortexr5            = "${TUNE_FEATURES:tune-armv7r-vfpv3d16} cortexr5 idiv"
PACKAGE_EXTRA_ARCHS:tune-cortexr5      = "${PACKAGE_EXTRA_ARCHS:tune-armv7r-vfpv3d16} cortexr5-vfpv3d16"
