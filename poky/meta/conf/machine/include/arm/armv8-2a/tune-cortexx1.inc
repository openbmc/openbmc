#
# Tune Settings for cortex-x1
#
DEFAULTTUNE ?= "cortexx1"

TUNEVALID[cortexx1] = "Enable cortex-x1 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'cortexx1', ' -mcpu=cortex-x1', '', d)}"

require conf/machine/include/arm/arch-armv8-2a.inc

AVAILTUNES                                        += "cortexx1"
ARMPKGARCH:tune-cortexx1                           = "cortexx1"
TUNE_FEATURES:tune-cortexx1                        = "aarch64 crypto cortexx1"
PACKAGE_EXTRA_ARCHS:tune-cortexx1                  = "${PACKAGE_EXTRA_ARCHS:tune-armv8-2a-crypto} cortexx1"
BASE_LIB:tune-cortexx1                             = "lib64"
