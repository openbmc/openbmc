#
# Defaults for ARMv8-r
#
DEFAULTTUNE ?= "armv8r"

TUNEVALID[armv8r] = "Enable instructions for ARMv8-r"
TUNE_CCARGS_MARCH .= "${@bb.utils.contains('TUNE_FEATURES', 'armv8r', ' -march=armv8-r', '', d)}"
MACHINEOVERRIDES =. "${@bb.utils.contains('TUNE_FEATURES', 'armv8r', 'armv8r:', '', d)}"

require conf/machine/include/arm/arch-arm64.inc
require conf/machine/include/arm/feature-arm-crc.inc
require conf/machine/include/arm/feature-arm-crypto.inc
require conf/machine/include/arm/feature-arm-sve.inc

# All ARMv8 has floating point hardware built in.  Null it here to avoid any confusion for 32bit.
TARGET_FPU_32 = ""

AVAILTUNES += "armv8r armv8r-crc armv8r-crypto armv8r-simd armv8r-crc-crypto armv8r-crc-simd armv8r-crc-crypto-simd"
ARMPKGARCH:tune-armv8r                          = "armv8r"
ARMPKGARCH:tune-armv8r-crc                      = "armv8r"
ARMPKGARCH:tune-armv8r-crypto                   = "armv8r"
ARMPKGARCH:tune-armv8r-simd                     = "armv8r"
ARMPKGARCH:tune-armv8r-crc-crypto               = "armv8r"
ARMPKGARCH:tune-armv8r-crc-simd                 = "armv8r"
ARMPKGARCH:tune-armv8r-crc-crypto-simd          = "armv8r"
TUNE_FEATURES:tune-armv8r                       = "armv8r"
TUNE_FEATURES:tune-armv8r-crc                   = "${TUNE_FEATURES:tune-armv8r} crc"
TUNE_FEATURES:tune-armv8r-crypto                = "${TUNE_FEATURES:tune-armv8r} crypto"
TUNE_FEATURES:tune-armv8r-simd                  = "${TUNE_FEATURES:tune-armv8r} simd"
TUNE_FEATURES:tune-armv8r-crc-crypto            = "${TUNE_FEATURES:tune-armv8r-crc} crypto"
TUNE_FEATURES:tune-armv8r-crc-simd              = "${TUNE_FEATURES:tune-armv8r-crc} simd"
TUNE_FEATURES:tune-armv8r-crc-crypto-simd       = "${TUNE_FEATURES:tune-armv8r-crc-crypto} simd"
PACKAGE_EXTRA_ARCHS:tune-armv8r                 = "armv8r"
PACKAGE_EXTRA_ARCHS:tune-armv8r-crc             = "${PACKAGE_EXTRA_ARCHS:tune-armv8r} armv8r-crc"
PACKAGE_EXTRA_ARCHS:tune-armv8r-crypto          = "${PACKAGE_EXTRA_ARCHS:tune-armv8r} armv8r-crypto"
PACKAGE_EXTRA_ARCHS:tune-armv8r-simd            = "${PACKAGE_EXTRA_ARCHS:tune-armv8r} armv8r-simd"
PACKAGE_EXTRA_ARCHS:tune-armv8r-crc-simd        = "${PACKAGE_EXTRA_ARCHS:tune-armv8r-crc} armv8r-simd armv8r-crc-simd"
PACKAGE_EXTRA_ARCHS:tune-armv8r-crc-crypto-simd = "${PACKAGE_EXTRA_ARCHS:tune-armv8r-crc-simd} armv8r-crc-crypto-simd"
