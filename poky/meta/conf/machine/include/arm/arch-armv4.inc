DEFAULTTUNE ?= "armv4"

TUNEVALID[arm] = "Enable ARM instruction set"
TUNEVALID[armv4] = "Enable instructions for ARMv4"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'armv4', ' -march=armv4t', '', d)}"
# enable --fix-v4bx when we have armv4 in TUNE_FEATURES, but then disable it when we have also armv5 or thumb
# maybe we should extend bb.utils.contains to support check for any checkvalues in value, now it does 
# checkvalues.issubset(val) which cannot be used for negative test of foo neither bar in value
FIX_V4BX_ARMV4 = "${@bb.utils.contains('TUNE_FEATURES', 'armv4', '--fix-v4bx', '', d)}"
FIX_V4BX_ARMV5 = "${@bb.utils.contains('TUNE_FEATURES', 'armv5', '', '${FIX_V4BX_ARMV4}', d)}"
FIX_V4BX = "${@bb.utils.contains('TUNE_FEATURES', 'thumb', '', '${FIX_V4BX_ARMV5}', d)}"
TARGET_LD_KERNEL_ARCH += "${FIX_V4BX}"
MACHINEOVERRIDES =. "${@bb.utils.contains('TUNE_FEATURES', 'armv4', 'armv4:', '', d)}"

require conf/machine/include/arm/arch-arm.inc
require conf/machine/include/arm/feature-arm-thumb.inc

# Little Endian
AVAILTUNES += "armv4 armv4t"
ARMPKGARCH_tune-armv4  ?= "armv4"
ARMPKGARCH_tune-armv4t ?= "armv4"
TUNE_FEATURES_tune-armv4  = "arm armv4"
TUNE_FEATURES_tune-armv4t = "${TUNE_FEATURES_tune-armv4} thumb"
PACKAGE_EXTRA_ARCHS_tune-armv4  = "arm armv4"
PACKAGE_EXTRA_ARCHS_tune-armv4t = "${PACKAGE_EXTRA_ARCHS_tune-armv4} armv4t"

# Big Endian
AVAILTUNES += "armv4b armv4tb"
ARMPKGARCH_tune-armv4b  ?= "armv4"
ARMPKGARCH_tune-armv4tb ?= "armv4"
TUNE_FEATURES_tune-armv4b  = "${TUNE_FEATURES_tune-armv4}  bigendian"
TUNE_FEATURES_tune-armv4tb = "${TUNE_FEATURES_tune-armv4t} bigendian"
PACKAGE_EXTRA_ARCHS_tune-armv4b  = "armeb armv4b"
PACKAGE_EXTRA_ARCHS_tune-armv4tb = "${PACKAGE_EXTRA_ARCHS_tune-armv4b} armv4tb"
