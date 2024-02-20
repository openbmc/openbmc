DEFAULTTUNE ?= "armv8-1a"

TUNEVALID[armv8-1a] = "Enable instructions for ARMv8.1-a"
TUNE_CCARGS_MARCH .= "${@bb.utils.contains('TUNE_FEATURES', 'armv8-1a', ' -march=armv8.1-a', '', d)}"
# TUNE crypto will be handled by arch-armv8a.inc below
MACHINEOVERRIDES =. "${@bb.utils.contains('TUNE_FEATURES', 'armv8-1a', 'armv8-1a:', '', d)}"

require conf/machine/include/arm/arch-armv8a.inc

AVAILTUNES                                  += "armv8-1a armv8-1a-crypto"
ARMPKGARCH:tune-armv8-1a                    ?= "armv8-1a"
ARMPKGARCH:tune-armv8-1a-crypto             ?= "armv8-1a"
TUNE_FEATURES:tune-armv8-1a                  = "aarch64 armv8-1a"
TUNE_FEATURES:tune-armv8-1a-crypto           = "${TUNE_FEATURES:tune-armv8-1a} crypto"
PACKAGE_EXTRA_ARCHS:tune-armv8-1a            = "${PACKAGE_EXTRA_ARCHS:tune-armv8a} armv8-1a"
PACKAGE_EXTRA_ARCHS:tune-armv8-1a-crypto     = "${PACKAGE_EXTRA_ARCHS:tune-armv8-1a} armv8-1a-crypto"
BASE_LIB:tune-armv8-1a                       = "lib64"
BASE_LIB:tune-armv8-1a-crypto                = "lib64"
