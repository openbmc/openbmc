#
# Tune Settings for Cortex-R52
#
DEFAULTTUNE ?= "cortexr52"

TUNEVALID[cortexr52] = "Enable Cortex-R52 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'cortexr52', ' -mcpu=cortex-r52', '', d)}"

require conf/machine/include/arm/arch-armv8r.inc

AVAILTUNES                             += "cortexr52"
ARMPKGARCH:tune-cortexr52               = "cortexr52"
# We do not want -march since -mcpu is added above to cover for it
TUNE_FEATURES:tune-cortexr52            = "aarch64 crc simd cortexr52"
PACKAGE_EXTRA_ARCHS:tune-cortexr52      = "${PACKAGE_EXTRA_ARCHS:tune-armv8r-crc-simd} cortexr52"

AVAILTUNES                             += "cortexr52hf"
ARMPKGARCH:tune-cortexr52hf             = "cortexr52"
TUNE_FEATURES:tune-cortexr52hf          = "${TUNE_FEATURES:tune-cortexr52} callconvention-hard"
PACKAGE_EXTRA_ARCHS:tune-cortexr52hf    = "cortexr52hf"
