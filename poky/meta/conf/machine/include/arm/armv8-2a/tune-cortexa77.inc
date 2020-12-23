#
# Tune Settings for Cortex-A77
#
DEFAULTTUNE                                        ?= "cortexa77"

TUNEVALID[cortexa77] = "Enable Cortex-A77 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'cortexa77', ' -mcpu=cortex-a77', '', d)}"

require conf/machine/include/arm/arch-armv8-2a.inc

# Little Endian base configs
AVAILTUNES                                         += "cortexa77"
ARMPKGARCH_tune-cortexa77                           = "cortexa77"
TUNE_FEATURES_tune-cortexa77                        = "${TUNE_FEATURES_tune-armv8-2a-crypto} cortexa77"
PACKAGE_EXTRA_ARCHS_tune-cortexa77                  = "${PACKAGE_EXTRA_ARCHS_tune-armv8-2a-crypto} cortexa77"
BASE_LIB_tune-cortexa77                             = "lib64"
