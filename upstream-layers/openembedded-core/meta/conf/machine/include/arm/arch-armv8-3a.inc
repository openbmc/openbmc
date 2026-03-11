DEFAULTTUNE ?= "armv8-3a"

TUNEVALID[armv8-3a] = "Enable instructions for ARMv8.3-a"
TUNE_CCARGS_MARCH .= "${@bb.utils.contains('TUNE_FEATURES', 'armv8-3a', ' -march=armv8.3-a', '', d)}"
# TUNE crypto will be handled by arch-armv8a.inc below
MACHINEOVERRIDES =. "${@bb.utils.contains('TUNE_FEATURES', 'armv8-3a', 'armv8-3a:', '', d)}"

require conf/machine/include/arm/arch-armv8a.inc

AVAILTUNES                                  += "armv8-3a armv8-3a-crypto armv8-3a-crypto-sve"
ARMPKGARCH:tune-armv8-3a                    ?= "armv8-3a"
ARMPKGARCH:tune-armv8-3a-crypto             ?= "armv8-3a"
ARMPKGARCH:tune-armv8-3a-crypto-sve         ?= "armv8-3a"
TUNE_FEATURES:tune-armv8-3a                  = "aarch64 armv8-3a"
TUNE_FEATURES:tune-armv8-3a-crypto           = "${TUNE_FEATURES:tune-armv8-3a} crypto"
TUNE_FEATURES:tune-armv8-3a-crypto-sve       = "${TUNE_FEATURES:tune-armv8-3a-crypto} sve"
PACKAGE_EXTRA_ARCHS:tune-armv8-3a            = "${PACKAGE_EXTRA_ARCHS:tune-armv8a} armv8-3a"
PACKAGE_EXTRA_ARCHS:tune-armv8-3a-crypto     = "${PACKAGE_EXTRA_ARCHS:tune-armv8-3a} armv8-3a-crypto"
PACKAGE_EXTRA_ARCHS:tune-armv8-3a-crypto-sve = "${PACKAGE_EXTRA_ARCHS:tune-armv8-3a-crypto} armv8-3a-crypto-sve"
BASE_LIB:tune-armv8-3a                       = "lib64"
BASE_LIB:tune-armv8-3a-crypto                = "lib64"
BASE_LIB:tune-armv8-3a-crypto-sve            = "lib64"
