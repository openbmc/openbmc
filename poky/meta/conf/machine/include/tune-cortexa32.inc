DEFAULTTUNE ?= "cortexa32"


TUNEVALID[cortexa32] = "Enable Cortex-A32 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'cortexa32', ' -mcpu=cortex-a32', '', d)}"

require conf/machine/include/arm/arch-armv8a.inc

# Little Endian base configs
AVAILTUNES += "cortexa32 cortexa32-crypto"
ARMPKGARCH_tune-cortexa32             = "cortexa32"
ARMPKGARCH_tune-cortexa32-crypto      = "cortexa32"
TUNE_FEATURES_tune-cortexa32          = "armv8a cortexa32 crc"
TUNE_FEATURES_tune-cortexa32-crypto   = "armv8a cortexa32 crc crypto"
PACKAGE_EXTRA_ARCHS_tune-cortexa32             = "${PACKAGE_EXTRA_ARCHS_tune-armv8a-crc} cortexa32"
PACKAGE_EXTRA_ARCHS_tune-cortexa32-crypto      = "${PACKAGE_EXTRA_ARCHS_tune-armv8a-crc-crypto} cortexa32 cortexa32-crypto"
BASE_LIB_tune-cortexa32               = "lib"
BASE_LIB_tune-cortexa32-crypto        = "lib"
