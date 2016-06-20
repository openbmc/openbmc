DEFAULTTUNE ?= "armv4"

require conf/machine/include/arm/arch-armv4.inc

TUNEVALID[strongarm] = "Enable Strongarm 1100 series processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'strongarm', ' -mcpu=strongarm1100', '', d)}"

AVAILTUNES += "strongarm"
ARMPKGARCH_tune-strongarm = "strongarm"
TUNE_FEATURES_tune-strongarm = "${TUNE_FEATURES_tune-armv4} strongarm"
PACKAGE_EXTRA_ARCHS_tune-strongarm = "${PACKAGE_EXTRA_ARCHS_tune-armv4} strongarm"
