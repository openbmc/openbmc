#
# Tune Settings for cortex-a78c
#
DEFAULTTUNE ?= "cortexa78c"

TUNEVALID[cortexa78c] = "Enable cortex-a78c specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'cortexa78c', ' -mcpu=cortex-a78c', '', d)}"

require conf/machine/include/arm/arch-armv8-2a.inc

AVAILTUNES                                          += "cortexa78c"
ARMPKGARCH:tune-cortexa78c                           = "cortexa78c"
TUNE_FEATURES:tune-cortexa78c                        = "aarch64 crypto cortexa78c"
PACKAGE_EXTRA_ARCHS:tune-cortexa78c                  = "${PACKAGE_EXTRA_ARCHS:tune-armv8-2a-crypto} cortexa78c"
BASE_LIB:tune-cortexa78c                             = "lib64"
