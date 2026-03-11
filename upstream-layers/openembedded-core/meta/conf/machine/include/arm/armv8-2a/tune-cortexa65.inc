#
# Tune Settings for Cortex-A65
#
DEFAULTTUNE ?= "cortexa65"

TUNEVALID[cortexa65] = "Enable Cortex-A65 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'cortexa65', ' -mcpu=cortex-a65', '', d)}"

require conf/machine/include/arm/arch-armv8-2a.inc

# Little Endian base configs
AVAILTUNES                                         += "cortexa65"
ARMPKGARCH:tune-cortexa65                           = "cortexa65"
# We do not want -march since -mcpu is added above to cover for it
TUNE_FEATURES:tune-cortexa65                        = "aarch64 crypto cortexa65"
PACKAGE_EXTRA_ARCHS:tune-cortexa65                  = "${PACKAGE_EXTRA_ARCHS:tune-armv8-2a-crypto} cortexa65"
BASE_LIB:tune-cortexa65                             = "lib64"
