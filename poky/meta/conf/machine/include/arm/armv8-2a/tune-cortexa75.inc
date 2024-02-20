#
# Tune Settings for Cortex-A75
#
DEFAULTTUNE ?= "cortexa75"

TUNEVALID[cortexa75] = "Enable Cortex-A75 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'cortexa75', ' -mcpu=cortex-a75', '', d)}"

require conf/machine/include/arm/arch-armv8-2a.inc

# Little Endian base configs
AVAILTUNES                                         += "cortexa75"
ARMPKGARCH:tune-cortexa75                           = "cortexa75"
# We do not want -march since -mcpu is added above to cover for it
TUNE_FEATURES:tune-cortexa75                        = "aarch64 crypto cortexa75"
PACKAGE_EXTRA_ARCHS:tune-cortexa75                  = "${PACKAGE_EXTRA_ARCHS:tune-armv8-2a-crypto} cortexa75"
BASE_LIB:tune-cortexa75                             = "lib64"
