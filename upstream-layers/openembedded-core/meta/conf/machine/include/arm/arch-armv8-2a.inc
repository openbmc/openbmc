DEFAULTTUNE ?= "armv8-2a"

TUNEVALID[armv8-2a] = "Enable instructions for ARMv8.2-a"
TUNE_CCARGS_MARCH .= "${@bb.utils.contains('TUNE_FEATURES', 'armv8-2a', ' -march=armv8.2-a', '', d)}"
# TUNE crypto will be handled by arch-armv8a.inc below
MACHINEOVERRIDES =. "${@bb.utils.contains('TUNE_FEATURES', 'armv8-2a', 'armv8-2a:', '', d)}"

require conf/machine/include/arm/arch-armv8a.inc
require conf/machine/include/arm/feature-arm-sve.inc

# Little Endian base configs
AVAILTUNES += "armv8-2a armv8-2a-crypto"
ARMPKGARCH:tune-armv8-2a                    ?= "armv8-2a"
ARMPKGARCH:tune-armv8-2a-crypto             ?= "armv8-2a"
TUNE_FEATURES:tune-armv8-2a                  = "aarch64 armv8-2a"
TUNE_FEATURES:tune-armv8-2a-crypto           = "${TUNE_FEATURES:tune-armv8-2a} crypto"
PACKAGE_EXTRA_ARCHS:tune-armv8-2a            = "${PACKAGE_EXTRA_ARCHS:tune-armv8a} armv8-2a"
PACKAGE_EXTRA_ARCHS:tune-armv8-2a-crypto     = "${PACKAGE_EXTRA_ARCHS:tune-armv8-2a} armv8-2a-crypto"
BASE_LIB:tune-armv8-2a                       = "lib64"
BASE_LIB:tune-armv8-2a-crypto                = "lib64"
