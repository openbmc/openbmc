#
# Tune Settings for cortex-r82
#
DEFAULTTUNE ?= "cortexr82"

TUNEVALID[cortexr82] = "Enable cortex-r82 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'cortexr82', ' -mcpu=cortex-r82', '', d)}"

require conf/machine/include/arm/arch-armv8r.inc

AVAILTUNES                                         += "cortexr82"
ARMPKGARCH:tune-cortexr82                           = "cortexr82"
TUNE_FEATURES:tune-cortexr82                        = "${TUNE_FEATURES:tune-armv8r-crc-simd} cortexr82"
PACKAGE_EXTRA_ARCHS:tune-cortexr82                  = "${PACKAGE_EXTRA_ARCHS:tune-armv8r-crc-simd} cortexr82"
BASE_LIB:tune-cortexr82                             = "lib64"
