#
# Tune Settings for Cortex-R52
#
DEFAULTTUNE ?= "cortexr52"

TUNEVALID[cortexr52] = "Enable Cortex-R52 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'cortexr52', ' -mcpu=cortex-r52', '', d)}"

require conf/machine/include/arm/arch-armv8r.inc

AVAILTUNES                             += "cortexr52"
ARMPKGARCH:tune-cortexr52               = "cortexr52"
TUNE_FEATURES:tune-cortexr52            = "${TUNE_FEATURES:tune-armv8r-crc-simd} cortexr52"
PACKAGE_EXTRA_ARCHS:tune-cortexr52      = "${PACKAGE_EXTRA_ARCHS:tune-armv8r-crc-simd} cortexr52"
