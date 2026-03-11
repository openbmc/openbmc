#
# Tune Settings for cortex-a510
#
DEFAULTTUNE ?= "cortexa510"

TUNEVALID[cortexa510] = "Enable cortex-a510 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'cortexa510', ' -mcpu=cortex-a510', '', d)}"

require conf/machine/include/arm/arch-armv9a.inc

AVAILTUNES                                          += "cortexa510"
ARMPKGARCH:tune-cortexa510                           = "cortexa510"
TUNE_FEATURES:tune-cortexa510                        = "aarch64 crypto cortexa510"
PACKAGE_EXTRA_ARCHS:tune-cortexa510                  = "${PACKAGE_EXTRA_ARCHS:tune-armv9a-crypto} cortexa510"
BASE_LIB:tune-cortexa510                             = "lib64"
