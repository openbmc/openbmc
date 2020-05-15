DEFAULTTUNE ?= "cortexa73-cortexa53"

require conf/machine/include/arm/arch-armv8a.inc

TUNEVALID[cortexa73-cortexa53] = "Enable big.LITTLE Cortex-A73.Cortex-A53 specific processor optimizations"

TUNECONFLICTS[aarch64] = "armv4 armv5 armv6 armv7 armv7a"

MACHINEOVERRIDES =. "${@bb.utils.contains("TUNE_FEATURES", "cortexa73-cortexa53", "cortexa73-cortexa53:", "" ,d)}"

TUNE_CCARGS .= "${@bb.utils.contains("TUNE_FEATURES", "cortexa73-cortexa53", " -mtune=cortex-a73.cortex-a53", "", d)}"

# cortexa73.cortexa53 implies crc support
AVAILTUNES += "cortexa73-cortexa53 cortexa73-cortexa53-crypto"
ARMPKGARCH_tune-cortexa73-cortexa53                  = "cortexa73-cortexa53"
ARMPKGARCH_tune-cortexa73-cortexa53-crypto           = "cortexa73-cortexa53"
TUNE_FEATURES_tune-cortexa73-cortexa53               = "${TUNE_FEATURES_tune-armv8a-crc} cortexa73-cortexa53"
TUNE_FEATURES_tune-cortexa73-cortexa53-crypto        = "${TUNE_FEATURES_tune-armv8a-crc-crypto} cortexa73-cortexa53"
PACKAGE_EXTRA_ARCHS_tune-cortexa73-cortexa53         = "${PACKAGE_EXTRA_ARCHS_tune-armv8a-crc}        cortexa73-cortexa53"
PACKAGE_EXTRA_ARCHS_tune-cortexa73-cortexa53-crypto  = "${PACKAGE_EXTRA_ARCHS_tune-armv8a-crc-crypto} cortexa73-cortexa53 cortexa73-cortexa53-crypto"
BASE_LIB_tune-cortexa73-cortexa53                    = "lib64"
BASE_LIB_tune-cortexa73-cortexa53-crypto             = "lib64"

