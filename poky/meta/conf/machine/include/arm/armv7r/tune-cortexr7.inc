#
# Tune Settings for Cortex-R7
#
DEFAULTTUNE ?= "cortexr7"

TUNEVALID[cortexr7] = "Enable Cortex-R7 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'cortexr7', ' -mcpu=cortex-r7', '', d)}"

require conf/machine/include/arm/arch-armv7r.inc

AVAILTUNES                            += "cortexr7"
ARMPKGARCH:tune-cortexr7               = "cortexr7"
TUNE_FEATURES:tune-cortexr7            = "${TUNE_FEATURES:tune-armv7r-vfpv3d16} cortexr7 idiv"
PACKAGE_EXTRA_ARCHS:tune-cortexr7      = "${PACKAGE_EXTRA_ARCHS:tune-armv7r-vfpv3d16} cortexr7-vfpv3d16"
