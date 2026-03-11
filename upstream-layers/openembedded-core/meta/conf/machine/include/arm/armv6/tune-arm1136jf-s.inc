DEFAULTTUNE ?= "armv6hf"

require conf/machine/include/arm/arch-armv6.inc

TUNEVALID[arm1136jfs] = "Enable arm1136jfs specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'arm1136jfs', ' -mcpu=arm1136jf-s', '', d)}"
MACHINEOVERRIDES =. "${@bb.utils.contains('TUNE_FEATURES', 'arm1136jfs', 'armv6:', '', d)}"

AVAILTUNES += "arm1136jfs arm1136jfshf"
ARMPKGARCH:tune-arm1136jfs = "arm1136jfs"
ARMPKGARCH:tune-arm1136jfshf = "arm1136jfs"
# mcpu is used so don't use armv6 as we don't want march
TUNE_FEATURES:tune-arm1136jfs = "arm vfp arm1136jfs"
TUNE_FEATURES:tune-arm1136jfshf = "${TUNE_FEATURES:tune-arm1136jfs} callconvention-hard"
PACKAGE_EXTRA_ARCHS:tune-arm1136jfs = "${PACKAGE_EXTRA_ARCHS:tune-armv6} arm1136jfs-vfp"
PACKAGE_EXTRA_ARCHS:tune-arm1136jfshf = "${PACKAGE_EXTRA_ARCHS:tune-armv6hf} arm1136jfshf-vfp"
