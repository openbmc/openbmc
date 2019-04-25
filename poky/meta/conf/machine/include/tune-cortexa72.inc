DEFAULTTUNE ?= "cortexa72"

TUNEVALID[cortexa72] = "Enable Cortex-A72 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'cortexa72', ' -mcpu=cortex-a72', '', d)}"

require conf/machine/include/arm/arch-armv8a.inc

# Little Endian base configs
AVAILTUNES += "cortexa72"
ARMPKGARCH_tune-cortexa72             = "cortexa72"
TUNE_FEATURES_tune-cortexa72          = "aarch64 cortexa72 crc crypto"
PACKAGE_EXTRA_ARCHS_tune-cortexa72    = "${PACKAGE_EXTRA_ARCHS_tune-armv8a-crc-crypto} cortexa72"
BASE_LIB_tune-cortexa72               = "lib64"
