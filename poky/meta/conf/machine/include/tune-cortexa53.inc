DEFAULTTUNE ?= "cortexa53"

TUNEVALID[cortexa53] = "Enable Cortex-A53 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'cortexa53', ' -mcpu=cortex-a53', '', d)}"

require conf/machine/include/arm/arch-armv8a.inc

# Little Endian base configs
AVAILTUNES += "cortexa53 cortexa53-crypto"
ARMPKGARCH_tune-cortexa53             = "cortexa53"
ARMPKGARCH_tune-cortexa53-crypto      = "cortexa53-crypto"
TUNE_FEATURES_tune-cortexa53          = "aarch64 cortexa53 crc"
TUNE_FEATURES_tune-cortexa53-crypto   = "${TUNE_FEATURES_tune-cortexa53} crypto"
PACKAGE_EXTRA_ARCHS_tune-cortexa53             = "${PACKAGE_EXTRA_ARCHS_tune-armv8a-crc} cortexa53"
PACKAGE_EXTRA_ARCHS_tune-cortexa53-crypto      = "${PACKAGE_EXTRA_ARCHS_tune-armv8a-crc-crypto} cortexa53 cortexa53-crypto"

BASE_LIB_tune-cortexa53               = "lib64"
BASE_LIB_tune-cortexa53-crypto        = "lib64"
