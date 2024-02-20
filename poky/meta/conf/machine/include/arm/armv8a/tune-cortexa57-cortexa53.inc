DEFAULTTUNE ?= "cortexa57-cortexa53"

TUNEVALID[cortexa57-cortexa53] = "Enable big.LITTLE Cortex-A57.Cortex-A53 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains("TUNE_FEATURES", "cortexa57-cortexa53", " -mcpu=cortex-a57.cortex-a53", "", d)}"
MACHINEOVERRIDES =. "${@bb.utils.contains("TUNE_FEATURES", "cortexa57-cortexa53", "cortexa57-cortexa53:", "", d)}"

require conf/machine/include/arm/arch-armv8a.inc

# Little Endian base configs
AVAILTUNES += "cortexa57-cortexa53"
ARMPKGARCH:tune-cortexa57-cortexa53 = "cortexa57-cortexa53"
# We do not want -march since -mcpu is added above to cover for it
TUNE_FEATURES:tune-cortexa57-cortexa53 = "aarch64 crc cortexa57-cortexa53"
PACKAGE_EXTRA_ARCHS:tune-cortexa57-cortexa53 = "${PACKAGE_EXTRA_ARCHS:tune-armv8a-crc} cortexa57-cortexa53"
BASE_LIB:tune-cortexa57-cortexa53 = "lib64"
