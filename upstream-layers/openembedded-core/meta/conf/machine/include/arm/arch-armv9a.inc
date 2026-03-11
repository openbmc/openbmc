DEFAULTTUNE ?= "armv9a"

TUNEVALID[armv9a] = "Enable instructions for ARMv9-a"
TUNE_CCARGS_MARCH .= "${@bb.utils.contains('TUNE_FEATURES', 'armv9a', ' -march=armv9-a', '', d)}"
MACHINEOVERRIDES =. "${@bb.utils.contains('TUNE_FEATURES', 'armv9a', 'armv9a:', '', d)}"

require conf/machine/include/arm/arch-arm64.inc
require conf/machine/include/arm/feature-arm-crypto.inc

# Little Endian base configs
AVAILTUNES += "armv9a armv9a-crypto"
ARMPKGARCH:tune-armv9a                     ?= "armv9a"
ARMPKGARCH:tune-armv9a-crypto              ?= "armv9a"
TUNE_FEATURES:tune-armv9a                  = "aarch64 armv9a"
TUNE_FEATURES:tune-armv9a-crypto           = "${TUNE_FEATURES:tune-armv9a} crypto"
PACKAGE_EXTRA_ARCHS:tune-armv9a            = "aarch64 armv9a"
PACKAGE_EXTRA_ARCHS:tune-armv9a-crypto     = "${PACKAGE_EXTRA_ARCHS:tune-armv9a} armv9a-crypto"
BASE_LIB:tune-armv9a                       = "lib64"
BASE_LIB:tune-armv9a-crypto                = "lib64"
