DEFAULTTUNE ?= "cortexa55"

TUNEVALID[cortexa55] = "Enable Cortex-A55 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'cortexa55', ' -mcpu=cortex-a55', '', d)}"

require conf/machine/include/arm/arch-armv8-2a.inc

# Little Endian base configs
AVAILTUNES += "cortexa55"
ARMPKGARCH:tune-cortexa55             = "cortexa55"
# We do not want -march since -mcpu is added above to cover for it
TUNE_FEATURES:tune-cortexa55          = "aarch64 crypto cortexa55"
PACKAGE_EXTRA_ARCHS:tune-cortexa55    = "${PACKAGE_EXTRA_ARCHS:tune-armv8-2a-crypto} cortexa55"
BASE_LIB:tune-cortexa55               = "lib64"
