DEFAULTTUNE ?= "cortexa72"

TUNEVALID[cortexa72] = "Enable Cortex-A72 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'cortexa72', ' -mcpu=cortex-a72', '', d)}"

require conf/machine/include/arm/arch-armv8a.inc

# Little Endian base configs
AVAILTUNES += "cortexa72 cortexa72-crypto"
ARMPKGARCH:tune-cortexa72             = "cortexa72"
ARMPKGARCH:tune-cortexa72-crypto      = "cortexa72"
# We do not want -march since -mcpu is added above to cover for it
TUNE_FEATURES:tune-cortexa72          = "aarch64 crc cortexa72"
TUNE_FEATURES:tune-cortexa72-crypto   = "${TUNE_FEATURES:tune-cortexa72} crypto"
PACKAGE_EXTRA_ARCHS:tune-cortexa72        = "${PACKAGE_EXTRA_ARCHS:tune-armv8a-crc} cortexa72"
PACKAGE_EXTRA_ARCHS:tune-cortexa72-crypto = "${PACKAGE_EXTRA_ARCHS:tune-armv8a-crc-crypto} cortexa72 cortexa72-crypto"
BASE_LIB:tune-cortexa72            = "lib64"
BASE_LIB:tune-cortexa72-crypto     = "lib64"
