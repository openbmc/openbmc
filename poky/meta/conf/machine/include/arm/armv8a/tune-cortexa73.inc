#
# Tune Settings for Cortex-A73
#
DEFAULTTUNE ?= "cortexa73"

TUNEVALID[cortexa73] = "Enable Cortex-A73 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'cortexa73', ' -mcpu=cortex-a73', '', d)}"

require conf/machine/include/arm/arch-armv8a.inc

# Little Endian base configs
AVAILTUNES                                += "cortexa73"
ARMPKGARCH_tune-cortexa73                  = "cortexa73"
TUNE_FEATURES_tune-cortexa73               = "${TUNE_FEATURES_tune-armv8a-crc-crypto} cortexa73"
PACKAGE_EXTRA_ARCHS_tune-cortexa73         = "${PACKAGE_EXTRA_ARCHS_tune-armv8a-crc-crypto} cortexa73"
BASE_LIB_tune-cortexa73                    = "lib64"
