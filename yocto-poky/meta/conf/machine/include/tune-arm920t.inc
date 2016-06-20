DEFAULTTUNE ?= "armv4t"

require conf/machine/include/arm/arch-armv4.inc

TUNEVALID[arm920t] = "Enable arm920t specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'arm920t', ' -mcpu=arm920t', '', d)}"

AVAILTUNES += "arm920t"
ARMPKGARCH_tune-arm920t = "arm920t"
TUNE_FEATURES_tune-arm920t = "${TUNE_FEATURES_tune-armv4t} arm920t"
PACKAGE_EXTRA_ARCHS_tune-arm920t = "${PACKAGE_EXTRA_ARCHS_tune-armv4t} arm920t arm920tt"
