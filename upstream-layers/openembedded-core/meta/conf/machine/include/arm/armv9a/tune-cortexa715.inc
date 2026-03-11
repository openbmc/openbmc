#
# Tune Settings for cortex-a715
#
DEFAULTTUNE ?= "cortexa715"

TUNEVALID[cortexa715] = "Enable cortex-a715 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'cortexa715', ' -mcpu=cortex-a715', '', d)}"

require conf/machine/include/arm/arch-armv9a.inc

AVAILTUNES                                          += "cortexa715"
ARMPKGARCH:tune-cortexa715                           = "cortexa715"
TUNE_FEATURES:tune-cortexa715                        = "aarch64 crypto cortexa715"
PACKAGE_EXTRA_ARCHS:tune-cortexa715                  = "${PACKAGE_EXTRA_ARCHS:tune-armv9a-crypto} cortexa715"
BASE_LIB:tune-cortexa715                             = "lib64"
