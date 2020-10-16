#
# Tune Settings for Cortex-A76
#
DEFAULTTUNE                                        ?= "cortexa76"

TUNEVALID[cortexa76] = "Enable Cortex-A76 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'cortexa76', ' -mcpu=cortex-a76', '', d)}"

require conf/machine/include/arm/arch-armv8-2a.inc

# Little Endian base configs
AVAILTUNES                                         += "cortexa76"
ARMPKGARCH_tune-cortexa76                           = "cortexa76"
TUNE_FEATURES_tune-cortexa76                        = "${TUNE_FEATURES_tune-armv8-2a-crypto} cortexa76"
PACKAGE_EXTRA_ARCHS_tune-cortexa76                  = "${PACKAGE_EXTRA_ARCHS_tune-armv8-2a-crypto} cortexa76"
BASE_LIB_tune-cortexa76                             = "lib64"
