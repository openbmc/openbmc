#
# Tune Settings for cortex-a78ae
#
DEFAULTTUNE ?= "cortexa78ae"

TUNEVALID[cortexa78ae] = "Enable cortex-a78ae specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'cortexa78ae', ' -mcpu=cortex-a78ae', '', d)}"

require conf/machine/include/arm/arch-armv8-2a.inc

AVAILTUNES                                           += "cortexa78ae"
ARMPKGARCH:tune-cortexa78ae                           = "cortexa78ae"
TUNE_FEATURES:tune-cortexa78ae                        = "aarch64 crypto cortexa78ae"
PACKAGE_EXTRA_ARCHS:tune-cortexa78ae                  = "${PACKAGE_EXTRA_ARCHS:tune-armv8-2a-crypto} cortexa78ae"
BASE_LIB:tune-cortexa78ae                             = "lib64"
