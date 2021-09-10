#
# Tune Settings for Cortex-R4F
#
DEFAULTTUNE ?= "cortexr4f"

TUNEVALID[cortexr4f] = "Enable Cortex-R4F specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'cortexr4f', ' -mcpu=cortex-r4f', '', d)}"

require conf/machine/include/arm/arch-armv7r.inc

AVAILTUNES                            += "cortexr4f"
ARMPKGARCH:tune-cortexr4f              = "cortexr4f"
TUNE_FEATURES:tune-cortexr4f           = "${TUNE_FEATURES:tune-armv7r-vfpv3d16} cortexr4f"
PACKAGE_EXTRA_ARCHS:tune-cortexr4f     = "${PACKAGE_EXTRA_ARCHS:tune-armv7r-vfpv3d16} cortexr4f-vfpv3d16"
