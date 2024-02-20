DEFAULTTUNE ?= "cortexa72-cortexa53"

TUNEVALID[cortexa72-cortexa53] = "Enable big.LITTLE Cortex-A72.Cortex-A53 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains("TUNE_FEATURES", "cortexa72-cortexa53", " -mcpu=cortex-a72.cortex-a53", "", d)}"
MACHINEOVERRIDES =. "${@bb.utils.contains("TUNE_FEATURES", "cortexa72-cortexa53", "cortexa72-cortexa53:", "", d)}"

require conf/machine/include/arm/arch-armv8a.inc

# cortexa72.cortexa53 implies crc support
AVAILTUNES += "cortexa72-cortexa53 cortexa72-cortexa53-crypto"
ARMPKGARCH:tune-cortexa72-cortexa53                  = "cortexa72-cortexa53"
ARMPKGARCH:tune-cortexa72-cortexa53-crypto           = "cortexa72-cortexa53-crypto"
# We do not want -march since -mcpu is added above to cover for it
TUNE_FEATURES:tune-cortexa72-cortexa53               = "aarch64 crc cortexa72-cortexa53"
TUNE_FEATURES:tune-cortexa72-cortexa53-crypto        = "${TUNE_FEATURES:tune-cortexa72-cortexa53} crypto"
PACKAGE_EXTRA_ARCHS:tune-cortexa72-cortexa53         = "${PACKAGE_EXTRA_ARCHS:tune-armv8a-crc}        cortexa72-cortexa53"
PACKAGE_EXTRA_ARCHS:tune-cortexa72-cortexa53-crypto  = "${PACKAGE_EXTRA_ARCHS:tune-armv8a-crc-crypto} cortexa72-cortexa53 cortexa72-cortexa53-crypto"
BASE_LIB:tune-cortexa72-cortexa53                    = "lib64"
BASE_LIB:tune-cortexa72-cortexa53-crypto             = "lib64"

