#
# Tune Settings for Cortex-A77
#
DEFAULTTUNE                                        ?= "cortexa77"

TUNEVALID[cortexa77] = "Enable Cortex-A77 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'cortexa77', ' -mcpu=cortex-a77', '', d)}"

require conf/machine/include/arm/arch-armv8-2a.inc

# Little Endian base configs
AVAILTUNES                                         += "cortexa77"
ARMPKGARCH:tune-cortexa77                           = "cortexa77"
# We do not want -march since -mcpu is added above to cover for it
TUNE_FEATURES:tune-cortexa77                        = "aarch64 crypto cortexa77"
PACKAGE_EXTRA_ARCHS:tune-cortexa77                  = "${PACKAGE_EXTRA_ARCHS:tune-armv8-2a-crypto} cortexa77"
BASE_LIB:tune-cortexa77                             = "lib64"
