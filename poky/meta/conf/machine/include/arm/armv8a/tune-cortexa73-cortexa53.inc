DEFAULTTUNE ?= "cortexa73-cortexa53"

TUNEVALID[cortexa73-cortexa53] = "Enable big.LITTLE Cortex-A73.Cortex-A53 specific processor optimizations"
MACHINEOVERRIDES =. "${@bb.utils.contains("TUNE_FEATURES", "cortexa73-cortexa53", "cortexa73-cortexa53:", "", d)}"
TUNE_CCARGS .= "${@bb.utils.contains("TUNE_FEATURES", "cortexa73-cortexa53", " -mcpu=cortex-a73.cortex-a53", "", d)}"

require conf/machine/include/arm/arch-armv8a.inc

# cortexa73.cortexa53 implies crc support
AVAILTUNES += "cortexa73-cortexa53 cortexa73-cortexa53-crypto"
ARMPKGARCH:tune-cortexa73-cortexa53                  = "cortexa73-cortexa53"
ARMPKGARCH:tune-cortexa73-cortexa53-crypto           = "cortexa73-cortexa53-crypto"
# We do not want -march since -mcpu is added above to cover for it
TUNE_FEATURES:tune-cortexa73-cortexa53               = "aarch64 crc cortexa73-cortexa53"
TUNE_FEATURES:tune-cortexa73-cortexa53-crypto        = "${TUNE_FEATURES:tune-cortexa73-cortexa53} crypto"
PACKAGE_EXTRA_ARCHS:tune-cortexa73-cortexa53         = "${PACKAGE_EXTRA_ARCHS:tune-armv8a-crc}        cortexa73-cortexa53"
PACKAGE_EXTRA_ARCHS:tune-cortexa73-cortexa53-crypto  = "${PACKAGE_EXTRA_ARCHS:tune-armv8a-crc-crypto} cortexa73-cortexa53 cortexa73-cortexa53-crypto"
BASE_LIB:tune-cortexa73-cortexa53                    = "lib64"
BASE_LIB:tune-cortexa73-cortexa53-crypto             = "lib64"

