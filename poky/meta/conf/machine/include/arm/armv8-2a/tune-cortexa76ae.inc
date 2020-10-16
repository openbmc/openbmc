#
# Tune Settings for Cortex-A76AE
#
DEFAULTTUNE                                        ?= "cortexa76ae"

TUNEVALID[cortexa76ae] = "Enable Cortex-A76AE specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'cortexa76ae', ' -mcpu=cortex-a76ae', '', d)}"

require conf/machine/include/arm/arch-armv8-2a.inc

# Little Endian base configs
AVAILTUNES                                         += "cortexa76ae"
ARMPKGARCH_tune-cortexa76ae                         = "cortexa76ae"
TUNE_FEATURES_tune-cortexa65ae                      = "${TUNE_FEATURES_tune-armv8-2a-crypto} cortexa76ae"
PACKAGE_EXTRA_ARCHS_tune-cortexa76ae                = "${PACKAGE_EXTRA_ARCHS_tune-armv8-2a-crypto} cortexa76ae"
BASE_LIB_tune-cortexa76ae                           = "lib64"
