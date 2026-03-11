#
# Tune Settings for Cortex-A76
#
DEFAULTTUNE                                        ?= "cortexa76"

TUNEVALID[cortexa76] = "Enable Cortex-A76 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'cortexa76', ' -mcpu=cortex-a76', '', d)}"

require conf/machine/include/arm/arch-armv8-2a.inc

# Little Endian base configs
AVAILTUNES                                         += "cortexa76"
ARMPKGARCH:tune-cortexa76                           = "cortexa76"
# We do not want -march since -mcpu is added above to cover for it
TUNE_FEATURES:tune-cortexa76                        = "aarch64 crypto cortexa76"
PACKAGE_EXTRA_ARCHS:tune-cortexa76                  = "${PACKAGE_EXTRA_ARCHS:tune-armv8-2a-crypto} cortexa76"
BASE_LIB:tune-cortexa76                             = "lib64"
