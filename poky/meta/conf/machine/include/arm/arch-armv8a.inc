DEFAULTTUNE ?= "armv8a-crc"

TUNEVALID[armv8a] = "Enable instructions for ARMv8-a"
TUNE_CCARGS_MARCH .= "${@bb.utils.contains('TUNE_FEATURES', 'armv8a', ' -march=armv8-a', '', d)}"
MACHINEOVERRIDES =. "${@bb.utils.contains('TUNE_FEATURES', 'armv8a', 'armv8a:', '', d)}"

require conf/machine/include/arm/arch-arm64.inc
require conf/machine/include/arm/feature-arm-crc.inc
require conf/machine/include/arm/feature-arm-crypto.inc

# Little Endian base configs
AVAILTUNES += "armv8a armv8a-crc armv8a-crc-crypto armv8a-crypto"
ARMPKGARCH:tune-armv8a                    ?= "armv8a"
ARMPKGARCH:tune-armv8a-crc                ?= "armv8a"
ARMPKGARCH:tune-armv8a-crypto             ?= "armv8a"
ARMPKGARCH:tune-armv8a-crc-crypto         ?= "armv8a"
TUNE_FEATURES:tune-armv8a                  = "aarch64 armv8a"
TUNE_FEATURES:tune-armv8a-crc              = "${TUNE_FEATURES:tune-armv8a} crc"
TUNE_FEATURES:tune-armv8a-crypto           = "${TUNE_FEATURES:tune-armv8a} crypto"
TUNE_FEATURES:tune-armv8a-crc-crypto       = "${TUNE_FEATURES:tune-armv8a-crc} crypto"
PACKAGE_EXTRA_ARCHS:tune-armv8a            = "aarch64 armv8a"
PACKAGE_EXTRA_ARCHS:tune-armv8a-crc        = "${PACKAGE_EXTRA_ARCHS:tune-armv8a} armv8a-crc"
PACKAGE_EXTRA_ARCHS:tune-armv8a-crypto     = "${PACKAGE_EXTRA_ARCHS:tune-armv8a} armv8a-crypto"
PACKAGE_EXTRA_ARCHS:tune-armv8a-crc-crypto = "${PACKAGE_EXTRA_ARCHS:tune-armv8a-crc} armv8a-crypto armv8a-crc-crypto"
BASE_LIB:tune-armv8a                       = "lib64"
BASE_LIB:tune-armv8a-crc                   = "lib64"
BASE_LIB:tune-armv8a-crypto                = "lib64"
BASE_LIB:tune-armv8a-crc-crypto            = "lib64"
