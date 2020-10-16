#
# Tune Settings for Cortex-A75
#
DEFAULTTUNE ?= "cortexa75"

TUNEVALID[cortexa75] = "Enable Cortex-A75 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'cortexa75', ' -mcpu=cortex-a75', '', d)}"

require conf/machine/include/arm/arch-armv8-2a.inc

# Little Endian base configs
AVAILTUNES                                         += "cortexa75"
ARMPKGARCH_tune-cortexa75                           = "cortexa75"
TUNE_FEATURES_tune-cortexa75                        = "${TUNE_FEATURES_tune-armv8-2a-crypto} cortexa75"
PACKAGE_EXTRA_ARCHS_tune-cortexa75                  = "${PACKAGE_EXTRA_ARCHS_tune-armv8-2a-crypto} cortexa75"
BASE_LIB_tune-cortexa75                             = "lib64"
