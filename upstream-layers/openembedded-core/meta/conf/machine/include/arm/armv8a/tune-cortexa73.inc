#
# Tune Settings for Cortex-A73
#
DEFAULTTUNE ?= "cortexa73"

TUNEVALID[cortexa73] = "Enable Cortex-A73 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'cortexa73', ' -mcpu=cortex-a73', '', d)}"

require conf/machine/include/arm/arch-armv8a.inc

# Little Endian base configs
AVAILTUNES                                += "cortexa73 cortexa73-crypto"
ARMPKGARCH:tune-cortexa73                  = "cortexa73"
ARMPKGARCH:tune-cortexa73-crypto           = "cortexa73"
# We do not want -march since -mcpu is added above to cover for it
TUNE_FEATURES:tune-cortexa73               = "aarch64 crc cortexa73"
TUNE_FEATURES:tune-cortexa73-crypto        = "${TUNE_FEATURES:tune-cortexa73} crypto"
PACKAGE_EXTRA_ARCHS:tune-cortexa73         = "${PACKAGE_EXTRA_ARCHS:tune-armv8a-crc} cortexa73"
PACKAGE_EXTRA_ARCHS:tune-cortexa73-crypto  = "${PACKAGE_EXTRA_ARCHS:tune-armv8a-crc-crypto} cortexa73 cortexa73-crypto"
BASE_LIB:tune-cortexa73                    = "lib64"
BASE_LIB:tune-cortexa73-crypto             = "lib64"
