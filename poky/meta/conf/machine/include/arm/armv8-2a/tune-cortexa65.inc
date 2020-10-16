#
# Tune Settings for Cortex-A65
#
DEFAULTTUNE ?= "cortexa65"

TUNEVALID[cortexa65] = "Enable Cortex-A65 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'cortexa65', ' -mcpu=cortex-a65', '', d)}"

require conf/machine/include/arm/arch-armv8-2a.inc

# Little Endian base configs
AVAILTUNES                                         += "cortexa65"
ARMPKGARCH_tune-cortexa65                           = "cortexa65"
TUNE_FEATURES_tune-cortexa65                        = "${TUNE_FEATURES_tune-armv8-2a-crypto} cortexa65"
PACKAGE_EXTRA_ARCHS_tune-cortexa65                  = "${PACKAGE_EXTRA_ARCHS_tune-armv8-2a-crypto} cortexa65"
BASE_LIB_tune-cortexa65                             = "lib64"
