#
# Tune Settings for big.LITTLE Cortex-A73 - Cortex-A35
#
DEFAULTTUNE ?= "cortexa73-cortexa35"

TUNEVALID[cortexa73-cortexa35] = "Enable big.LITTLE Cortex-A73.Cortex-A35 specific processor optimizations"
MACHINEOVERRIDES =. "${@bb.utils.contains("TUNE_FEATURES", "cortexa73-cortexa35", "cortexa73-cortexa35:", "", d)}"
TUNE_CCARGS .= "${@bb.utils.contains("TUNE_FEATURES", "cortexa73-cortexa35", " -mcpu=cortex-a73.cortex-a35", "", d)}"

require conf/machine/include/arm/arch-armv8a.inc

# cortexa73.cortexa35 implies crc support
AVAILTUNES                                          += "cortexa73-cortexa35 cortexa73-cortexa35-crypto"
ARMPKGARCH:tune-cortexa73-cortexa35                  = "cortexa73-cortexa35"
ARMPKGARCH:tune-cortexa73-cortexa35-crypto           = "cortexa73-cortexa35-crypto"
# We do not want -march since -mcpu is added above to cover for it
TUNE_FEATURES:tune-cortexa73-cortexa35               = "aarch64 crc cortexa73-cortexa35"
TUNE_FEATURES:tune-cortexa73-cortexa35-crypto        = "${TUNE_FEATURES:tune-cortexa73-cortexa35} crypto"
PACKAGE_EXTRA_ARCHS:tune-cortexa73-cortexa35         = "${PACKAGE_EXTRA_ARCHS:tune-armv8a-crc} cortexa73-cortexa35"
PACKAGE_EXTRA_ARCHS:tune-cortexa73-cortexa35-crypto  = "${PACKAGE_EXTRA_ARCHS:tune-armv8a-crc-crypto} cortexa73-cortexa35 cortexa73-cortexa35-crypto"
BASE_LIB:tune-cortexa73-cortexa35                    = "lib64"
BASE_LIB:tune-cortexa73-cortexa35-crypto             = "lib64"
