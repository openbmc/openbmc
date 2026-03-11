#
# Tune Settings for cortex-a710
#
DEFAULTTUNE ?= "cortexa710"

TUNEVALID[cortexa710] = "Enable cortex-a710 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'cortexa710', ' -mcpu=cortex-a710', '', d)}"

require conf/machine/include/arm/arch-armv9a.inc

AVAILTUNES                                          += "cortexa710"
ARMPKGARCH:tune-cortexa710                           = "cortexa710"
TUNE_FEATURES:tune-cortexa710                        = "aarch64 crypto cortexa710"
PACKAGE_EXTRA_ARCHS:tune-cortexa710                  = "${PACKAGE_EXTRA_ARCHS:tune-armv9a-crypto} cortexa710"
BASE_LIB:tune-cortexa710                             = "lib64"
