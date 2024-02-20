#
# Tune Settings for cortex-x3
#
DEFAULTTUNE ?= "cortexx3"

TUNEVALID[cortexx3] = "Enable cortex-x3 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'cortexx3', ' -mcpu=cortex-x3', '', d)}"

require conf/machine/include/arm/arch-armv9a.inc

AVAILTUNES                                        += "cortexx3"
ARMPKGARCH:tune-cortexx3                           = "cortexx3"
TUNE_FEATURES:tune-cortexx3                        = "aarch64 crypto cortexx3"
PACKAGE_EXTRA_ARCHS:tune-cortexx3                  = "${PACKAGE_EXTRA_ARCHS:tune-armv9a-crypto} cortexx3"
BASE_LIB:tune-cortexx3                             = "lib64"
