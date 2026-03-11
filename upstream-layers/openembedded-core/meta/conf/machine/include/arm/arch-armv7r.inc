#
# Defaults for ARMv7-r
#
DEFAULTTUNE ?= "armv7r"

TUNEVALID[armv7r] = "Enable instructions for ARMv7-r"
TUNE_CCARGS_MARCH = "${@bb.utils.contains('TUNE_FEATURES', 'armv7r', ' -march=armv7-r', '', d)}"
MACHINEOVERRIDES =. "${@bb.utils.contains('TUNE_FEATURES', 'armv7r', 'armv7r:', '', d)}"

TUNECONFLICTS[armv7r] = "armv4 armv5 armv6 armv7a"

require conf/machine/include/arm/arch-armv6.inc
require conf/machine/include/arm/feature-arm-idiv.inc
require conf/machine/include/arm/feature-arm-neon.inc

AVAILTUNES                            += "armv7r armv7r-vfpv3d16"
ARMPKGARCH:tune-armv7r                 = "armv7r"
ARMPKGARCH:tune-armv7r-vfpv3d16        = "armv7r"
TUNE_FEATURES:tune-armv7r              = "armv7r"
TUNE_FEATURES:tune-armv7r-vfpv3d16     = "${TUNE_FEATURES:tune-armv7r} vfpv3d16"
PACKAGE_EXTRA_ARCHS:tune-armv7r        = "armv7r"
PACKAGE_EXTRA_ARCHS:tune-armv7r-vfpv3d16  = "${PACKAGE_EXTRA_ARCHS:tune-armv7r} tune-armv7r-fpv3d16"
