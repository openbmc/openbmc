DEFAULTTUNE ?= "armv8-4a"

TUNEVALID[armv8-4a] = "Enable instructions for ARMv8.4-a"
TUNE_CCARGS_MARCH .= "${@bb.utils.contains('TUNE_FEATURES', 'armv8-4a', ' -march=armv8.4-a', '', d)}"
# TUNE crypto will be handled by arch-armv8a.inc below
MACHINEOVERRIDES =. "${@bb.utils.contains('TUNE_FEATURES', 'armv8-4a', 'armv8-4a:', '', d)}"

require conf/machine/include/arm/arch-armv8a.inc

# Little Endian base configs
AVAILTUNES += "armv8-4a armv8-4a-crypto"
ARMPKGARCH:tune-armv8-4a                    ?= "armv8-4a"
ARMPKGARCH:tune-armv8-4a-crypto             ?= "armv8-4a"
TUNE_FEATURES:tune-armv8-4a                  = "aarch64 armv8-4a"
TUNE_FEATURES:tune-armv8-4a-crypto           = "${TUNE_FEATURES:tune-armv8-4a} crypto"
PACKAGE_EXTRA_ARCHS:tune-armv8-4a            = "${PACKAGE_EXTRA_ARCHS:tune-armv8a} armv8-4a"
PACKAGE_EXTRA_ARCHS:tune-armv8-4a-crypto     = "${PACKAGE_EXTRA_ARCHS:tune-armv8-4a} armv8-4a-crypto"
BASE_LIB:tune-armv8-4a                       = "lib64"
BASE_LIB:tune-armv8-4a-crypto                = "lib64"
