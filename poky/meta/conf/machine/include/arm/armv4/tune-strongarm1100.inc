DEFAULTTUNE ?= "strongarm"

require conf/machine/include/arm/arch-armv4.inc

TUNEVALID[strongarm] = "Enable Strongarm 1100 series processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'strongarm', ' -mcpu=strongarm1100', '', d)}"
MACHINEOVERRIDES =. "${@bb.utils.contains('TUNE_FEATURES', 'strongarm', 'armv4:', '', d)}"

AVAILTUNES += "strongarm"
ARMPKGARCH:tune-strongarm = "strongarm"
TUNE_FEATURES:tune-strongarm = "arm strongarm"
PACKAGE_EXTRA_ARCHS:tune-strongarm = "${PACKAGE_EXTRA_ARCHS:tune-armv4} strongarm"
