#
# Tune Settings for big.LITTLE Cortex-A75 - Cortex-A55
#
DEFAULTTUNE                                        ?= "cortexa75-cortexa55"

TUNEVALID[cortexa75-cortexa55] = "Enable big.LITTLE Cortex-A75.Cortex-A55 specific processor optimizations"
MACHINEOVERRIDES =. "${@bb.utils.contains("TUNE_FEATURES", "cortexa75-cortexa55", "cortexa75-cortexa55:", "", d)}"
TUNE_CCARGS .= "${@bb.utils.contains("TUNE_FEATURES", "cortexa75-cortexa55", " -mcpu=cortex-a75.cortex-a55", "", d)}"

require conf/machine/include/arm/arch-armv8-2a.inc

AVAILTUNES                                         += "cortexa75-cortexa55 cortexa75-cortexa55-crypto"
ARMPKGARCH:tune-cortexa75-cortexa55                 = "cortexa75-cortexa55"
ARMPKGARCH:tune-cortexa75-cortexa55-crypto          = "cortexa75-cortexa55-crypto"
# We do not want -march since -mcpu is added above to cover for it
TUNE_FEATURES:tune-cortexa75-cortexa55              = "aarch64 cortexa75-cortexa55"
TUNE_FEATURES:tune-cortexa75-cortexa55-crypto       = "${TUNE_FEATURES:tune-cortexa75-cortexa55} crypto"
PACKAGE_EXTRA_ARCHS:tune-cortexa75-cortexa55        = "${PACKAGE_EXTRA_ARCHS:tune-armv8-2a} cortexa75-cortexa55"
PACKAGE_EXTRA_ARCHS:tune-cortexa75-cortexa55-crypto = "${PACKAGE_EXTRA_ARCHS:tune-armv8-2a-crypto} cortexa75-cortexa55 cortexa75-cortexa55-crypto"
BASE_LIB:tune-cortexa75-cortexa55                   = "lib64"
BASE_LIB:tune-cortexa75-cortexa55-crypto            = "lib64"
