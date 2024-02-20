#
# Tune Settings for Cortex-A78
#
DEFAULTTUNE                                        ?= "cortexa78"

TUNEVALID[cortexa78] = "Enable Cortex-A78 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'cortexa78', ' -mcpu=cortex-a78', '', d)}"

require conf/machine/include/arm/arch-armv8-2a.inc

# Little Endian base configs
AVAILTUNES                                         += "cortexa78"
ARMPKGARCH:tune-cortexa78                           = "cortexa78"
# We do not want -march since -mcpu is added above to cover for it
TUNE_FEATURES:tune-cortexa78                        = "aarch64 crypto cortexa78"
PACKAGE_EXTRA_ARCHS:tune-cortexa78                  = "${PACKAGE_EXTRA_ARCHS:tune-armv8-2a-crypto} cortexa78"
BASE_LIB:tune-cortexa78                             = "lib64"
