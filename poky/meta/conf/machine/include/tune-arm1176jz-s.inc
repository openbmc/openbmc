DEFAULTTUNE ?= "arm1176jzs"

require conf/machine/include/arm/arch-armv6.inc

TUNEVALID[arm1176jzs] = "Enable arm1176jzs specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'arm1176jzs', ' -mcpu=arm1176jz-s', '', d)}"
MACHINEOVERRIDES =. "${@bb.utils.contains('TUNE_FEATURES', 'arm1176jzs', 'armv6:', '', d)}"

AVAILTUNES += "arm1176jzs"
ARMPKGARCH_tune-arm1176jzs = "arm1176jzs"
TUNE_FEATURES_tune-arm1176jzs = "arm thumb arm1176jzs"
PACKAGE_EXTRA_ARCHS_tune-arm1176jzs = "${PACKAGE_EXTRA_ARCHS_tune-armv6t-novfp} arm1176jzs arm1176jzst"

AVAILTUNES += "arm1176jzs-be"
ARMPKGARCH_tune-arm1176jzs-be = "${ARMPKGARCH_tune-arm1176jzs}"
TUNE_FEATURES_tune-arm1176jzs-be = "${TUNE_FEATURES_tune-arm1176jzs} bigendian"
PACKAGE_EXTRA_ARCHS_tune-arm1176jzs-be = "${PACKAGE_EXTRA_ARCHS_tune-armv6tb-novfp} arm1176jzsb arm1176jzstb"
