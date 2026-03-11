#
# Tune Settings for cortex-x2
#
DEFAULTTUNE ?= "cortexx2"

TUNEVALID[cortexx2] = "Enable cortex-x2 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'cortexx2', ' -mcpu=cortex-x2', '', d)}"

require conf/machine/include/arm/arch-armv9a.inc

AVAILTUNES                                        += "cortexx2"
ARMPKGARCH:tune-cortexx2                           = "cortexx2"
TUNE_FEATURES:tune-cortexx2                        = "aarch64 crypto cortexx2"
PACKAGE_EXTRA_ARCHS:tune-cortexx2                  = "${PACKAGE_EXTRA_ARCHS:tune-armv9a-crypto} cortexx2"
BASE_LIB:tune-cortexx2                             = "lib64"
