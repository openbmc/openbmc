DEFAULTTUNE ?= "cortexa57"

TUNEVALID[cortexa57] = "Enable Cortex-A57 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'cortexa57', ' -mcpu=cortex-a57', '', d)}"

require conf/machine/include/arm/arch-armv8a.inc

# Little Endian base configs
AVAILTUNES += "cortexa57 cortexa57-crypto"
ARMPKGARCH_tune-cortexa57             = "cortexa57"
ARMPKGARCH_tune-cortexa57-crypto      = "cortexa57-crypto"
TUNE_FEATURES_tune-cortexa57          = "${TUNE_FEATURES_tune-armv8a-crc} cortexa57"
TUNE_FEATURES_tune-cortexa57-crypto   = "${TUNE_FEATURES_tune-cortexa57} crypto"
PACKAGE_EXTRA_ARCHS_tune-cortexa57             = "${PACKAGE_EXTRA_ARCHS_tune-armv8a-crc} cortexa57"
PACKAGE_EXTRA_ARCHS_tune-cortexa57-crypto      = "${PACKAGE_EXTRA_ARCHS_tune-armv8a-crc-crypto} cortexa57 cortexa57-crypto"
BASE_LIB_tune-cortexa57               = "lib64"
BASE_LIB_tune-cortexa57-crypto        = "lib64"
