#
# Tune Settings for Cortex-A34
#
DEFAULTTUNE ?= "cortexa34"

TUNEVALID[cortexa34] = "Enable Cortex-A34 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'cortexa34', ' -mcpu=cortex-a34', '', d)}"

require conf/machine/include/arm/arch-armv8a.inc

# Little Endian base configs
AVAILTUNES                                += "cortexa34 cortexa34-crypto"
ARMPKGARCH_tune-cortexa34                  = "cortexa34"
ARMPKGARCH_tune-cortexa34-crypto           = "cortexa34"
TUNE_FEATURES_tune-cortexa34               = "${TUNE_FEATURES_tune-armv8a-crc} cortexa34"
TUNE_FEATURES_tune-cortexa34-crypto        = "${TUNE_FEATURES_tune-cortexa34} crypto"
PACKAGE_EXTRA_ARCHS_tune-cortexa34         = "${PACKAGE_EXTRA_ARCHS_tune-armv8a-crc} cortexa34"
PACKAGE_EXTRA_ARCHS_tune-cortexa34-crypto  = "${PACKAGE_EXTRA_ARCHS_tune-armv8a-crc-crypto} cortexa34 cortexa34-crypto"
BASE_LIB_tune-cortexa34                    = "lib64"
BASE_LIB_tune-cortexa34-crypto             = "lib64"
