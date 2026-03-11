#
# Tune Settings for Cortex-M23
#
DEFAULTTUNE ?= "cortexm23"

TUNEVALID[cortexm23] = "Enable Cortex-M23 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'cortexm23', ' -mcpu=cortex-m23', '', d)}"

require conf/machine/include/arm/arch-armv8m-base.inc

AVAILTUNES                          += "cortexm23"
ARMPKGARCH:tune-cortexm23            = "cortexm23"
# We do not want -march since -mcpu is added above to cover for it
TUNE_FEATURES:tune-cortexm23         = "cortexm23"
PACKAGE_EXTRA_ARCHS:tune-cortexm23   = "${PACKAGE_EXTRA_ARCHS:tune-armv8m-base} cortexm23"
