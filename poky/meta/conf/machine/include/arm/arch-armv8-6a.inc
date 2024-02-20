DEFAULTTUNE ?= "armv8-6a"

TUNEVALID[armv8-6a] = "Enable instructions for ARMv8.6-a"
TUNE_CCARGS_MARCH .= "${@bb.utils.contains('TUNE_FEATURES', 'armv8-6a', ' -march=armv8.6-a', '', d)}"
# TUNE crypto will be handled by arch-armv8a.inc below
MACHINEOVERRIDES =. "${@bb.utils.contains('TUNE_FEATURES', 'armv8-6a', 'armv8-6a:', '', d)}"

require conf/machine/include/arm/arch-armv8a.inc

AVAILTUNES                                  += "armv8-6a armv8-6a-crypto armv8-6a-crypto-sve"
ARMPKGARCH:tune-armv8-6a                    ?= "armv8-6a"
ARMPKGARCH:tune-armv8-6a-crypto             ?= "armv8-6a"
ARMPKGARCH:tune-armv8-6a-crypto-sve         ?= "armv8-6a"
TUNE_FEATURES:tune-armv8-6a                  = "aarch64 armv8-6a"
TUNE_FEATURES:tune-armv8-6a-crypto           = "${TUNE_FEATURES:tune-armv8-6a} crypto"
TUNE_FEATURES:tune-armv8-6a-crypto-sve       = "${TUNE_FEATURES:tune-armv8-6a-crypto} sve"
PACKAGE_EXTRA_ARCHS:tune-armv8-6a            = "${PACKAGE_EXTRA_ARCHS:tune-armv8a} armv8-6a"
PACKAGE_EXTRA_ARCHS:tune-armv8-6a-crypto     = "${PACKAGE_EXTRA_ARCHS:tune-armv8-6a} armv8-6a-crypto"
PACKAGE_EXTRA_ARCHS:tune-armv8-6a-crypto-sve = "${PACKAGE_EXTRA_ARCHS:tune-armv8-6a-crypto} armv8-6a-crypto-sve"
BASE_LIB:tune-armv8-6a                       = "lib64"
BASE_LIB:tune-armv8-6a-crypto                = "lib64"
BASE_LIB:tune-armv8-6a-crypto-sve            = "lib64"
