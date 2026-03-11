#
# Tune Settings for Cortex-A34
#
DEFAULTTUNE ?= "cortexa34"

TUNEVALID[cortexa34] = "Enable Cortex-A34 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'cortexa34', ' -mcpu=cortex-a34', '', d)}"

require conf/machine/include/arm/arch-armv8a.inc

# Little Endian base configs
AVAILTUNES                                += "cortexa34 cortexa34-crypto"
ARMPKGARCH:tune-cortexa34                  = "cortexa34"
ARMPKGARCH:tune-cortexa34-crypto           = "cortexa34"
# We do not want -march since -mcpu is added above to cover for it
TUNE_FEATURES:tune-cortexa34               = "aarch64 crc cortexa34"
TUNE_FEATURES:tune-cortexa34-crypto        = "${TUNE_FEATURES:tune-cortexa34} crypto"
PACKAGE_EXTRA_ARCHS:tune-cortexa34         = "${PACKAGE_EXTRA_ARCHS:tune-armv8a-crc} cortexa34"
PACKAGE_EXTRA_ARCHS:tune-cortexa34-crypto  = "${PACKAGE_EXTRA_ARCHS:tune-armv8a-crc-crypto} cortexa34 cortexa34-crypto"
BASE_LIB:tune-cortexa34                    = "lib64"
BASE_LIB:tune-cortexa34-crypto             = "lib64"
