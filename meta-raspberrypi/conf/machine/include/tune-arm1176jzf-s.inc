DEFAULTTUNE ?= "arm1176jzfshf"

require conf/machine/include/arm/arch-armv6.inc

TUNEVALID[arm1176jzfs] = "Enable arm1176jzfs specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'arm1176jzfs', ' -mcpu=arm1176jzf-s', '', d)}"
MACHINEOVERRIDES =. "${@bb.utils.contains('TUNE_FEATURES', 'arm1176jzfs', 'armv6:', '', d)}"

AVAILTUNES += "arm1176jzfs"
ARMPKGARCH_tune-arm1176jzfs = "arm1176jzfs"
TUNE_FEATURES_tune-arm1176jzfs = "arm thumb vfp arm1176jzfs"
PACKAGE_EXTRA_ARCHS_tune-arm1176jzfs = "${PACKAGE_EXTRA_ARCHS_tune-armv6} arm1176jzfs"

AVAILTUNES += "arm1176jzfshf"
ARMPKGARCH_tune-arm1176jzfshf = "${ARMPKGARCH_tune-arm1176jzfs}"
TUNE_FEATURES_tune-arm1176jzfshf = "${TUNE_FEATURES_tune-arm1176jzfs} callconvention-hard"
PACKAGE_EXTRA_ARCHS_tune-arm1176jzfshf = "${PACKAGE_EXTRA_ARCHS_tune-armv6thf} arm1176jzfshf-vfp"
