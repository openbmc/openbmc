DEFAULTTUNE ?= "cortexa53"

TUNEVALID[cortexa53] = "Enable Cortex-A53 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'cortexa53', ' -mcpu=cortex-a53', '', d)}"

require conf/machine/include/arm/arch-armv8a.inc

# Little Endian base configs
AVAILTUNES += "cortexa53 cortexa53-crypto"
ARMPKGARCH:tune-cortexa53             = "cortexa53"
ARMPKGARCH:tune-cortexa53-crypto      = "cortexa53-crypto"
TUNE_FEATURES:tune-cortexa53          = "${TUNE_FEATURES:tune-armv8a-crc} cortexa53"
TUNE_FEATURES:tune-cortexa53-crypto   = "${TUNE_FEATURES:tune-cortexa53} crypto"
PACKAGE_EXTRA_ARCHS:tune-cortexa53             = "${PACKAGE_EXTRA_ARCHS:tune-armv8a-crc} cortexa53"
PACKAGE_EXTRA_ARCHS:tune-cortexa53-crypto      = "${PACKAGE_EXTRA_ARCHS:tune-armv8a-crc-crypto} cortexa53 cortexa53-crypto"
BASE_LIB:tune-cortexa53               = "lib64"
BASE_LIB:tune-cortexa53-crypto        = "lib64"
