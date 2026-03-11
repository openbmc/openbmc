#
# Tune Settings for Cortex-A65AE
#
DEFAULTTUNE                                        ?= "cortexa65ae"

TUNEVALID[cortexa65ae] = "Enable Cortex-A65AE specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'cortexa65ae', ' -mcpu=cortex-a65ae', '', d)}"

require conf/machine/include/arm/arch-armv8-2a.inc

# Little Endian base configs
AVAILTUNES                                         += "cortexa65ae"
ARMPKGARCH:tune-cortexa65ae                         = "cortexa65ae"
# We do not want -march since -mcpu is added above to cover for it
TUNE_FEATURES:tune-cortexa65ae                      = "aarch64 crypto cortexa65ae"
PACKAGE_EXTRA_ARCHS:tune-cortexa65ae                = "${PACKAGE_EXTRA_ARCHS:tune-armv8-2a-crypto} cortexa65ae"
BASE_LIB:tune-cortexa65ae                           = "lib64"
