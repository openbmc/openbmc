#
# Tune Settings for cortex-x1c
#
DEFAULTTUNE ?= "cortexx1c"

TUNEVALID[cortexx1c] = "Enable cortex-x1c specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'cortexx1c', ' -mcpu=cortex-x1c', '', d)}"

require conf/machine/include/arm/arch-armv8-2a.inc

AVAILTUNES                                         += "cortexx1c"
ARMPKGARCH:tune-cortexx1c                           = "cortexx1c"
TUNE_FEATURES:tune-cortexx1c                        = "aarch64 crypto cortexx1c"
PACKAGE_EXTRA_ARCHS:tune-cortexx1c                  = "${PACKAGE_EXTRA_ARCHS:tune-armv8-2a-crypto} cortexx1c"
BASE_LIB:tune-cortexx1c                             = "lib64"
