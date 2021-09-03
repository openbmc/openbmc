DEFAULTTUNE ?= "cortexa35"

TUNEVALID[cortexa35] = "Enable Cortex-A35 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'cortexa35', ' -mcpu=cortex-a35', '', d)}"

require conf/machine/include/arm/arch-armv8a.inc

# Little Endian base configs
AVAILTUNES += "cortexa35 cortexa35-crypto"
ARMPKGARCH:tune-cortexa35             = "cortexa35"
ARMPKGARCH:tune-cortexa35-crypto      = "cortexa35"
TUNE_FEATURES:tune-cortexa35          = "${TUNE_FEATURES:tune-armv8a-crc} cortexa35"
TUNE_FEATURES:tune-cortexa35-crypto   = "${TUNE_FEATURES:tune-cortexa35} crypto"
PACKAGE_EXTRA_ARCHS:tune-cortexa35             = "${PACKAGE_EXTRA_ARCHS:tune-armv8a-crc} cortexa35"
PACKAGE_EXTRA_ARCHS:tune-cortexa35-crypto      = "${PACKAGE_EXTRA_ARCHS:tune-armv8a-crc-crypto} cortexa35 cortexa35-crypto"
BASE_LIB:tune-cortexa35               = "lib64"
BASE_LIB:tune-cortexa35-crypto        = "lib64"
