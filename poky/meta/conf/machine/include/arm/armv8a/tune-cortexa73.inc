#
# Tune Settings for Cortex-A73
#
DEFAULTTUNE ?= "cortexa73"

TUNEVALID[cortexa73] = "Enable Cortex-A73 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'cortexa73', ' -mcpu=cortex-a73', '', d)}"

require conf/machine/include/arm/arch-armv8a.inc

# Little Endian base configs
AVAILTUNES                                += "cortexa73"
ARMPKGARCH:tune-cortexa73                  = "cortexa73"
TUNE_FEATURES:tune-cortexa73               = "${TUNE_FEATURES:tune-armv8a-crc-crypto} cortexa73"
PACKAGE_EXTRA_ARCHS:tune-cortexa73         = "${PACKAGE_EXTRA_ARCHS:tune-armv8a-crc-crypto} cortexa73"
BASE_LIB:tune-cortexa73                    = "lib64"
