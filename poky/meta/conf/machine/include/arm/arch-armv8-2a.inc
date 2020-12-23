DEFAULTTUNE ?= "armv8-2a"

TUNEVALID[armv8-2a] = "Enable instructions for ARMv8-a"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'armv8-2a', ' -march=armv8.2-a', '', d)}"
# TUNE crypto will be handled by arch-armv8a.inc below
MACHINEOVERRIDES =. "${@bb.utils.contains('TUNE_FEATURES', 'armv8-2a', 'armv8-2a:', '', d)}"

require conf/machine/include/arm/arch-armv8a.inc

# Little Endian base configs
AVAILTUNES += "armv8-2a armv8-2a-crypto"
ARMPKGARCH_tune-armv8-2a                    ?= "armv8-2a"
ARMPKGARCH_tune-armv8-2a-crypto             ?= "armv8-2a"
TUNE_FEATURES_tune-armv8-2a                  = "aarch64 armv8-2a"
TUNE_FEATURES_tune-armv8-2a-crypto           = "${TUNE_FEATURES_tune-armv8-2a} crypto"
PACKAGE_EXTRA_ARCHS_tune-armv8-2a            = "${PACKAGE_EXTRA_ARCHS_tune-armv8a} armv8-2a"
PACKAGE_EXTRA_ARCHS_tune-armv8-2a-crypto     = "${PACKAGE_EXTRA_ARCHS_tune-armv8-2a} armv8-2a-crypto"
BASE_LIB_tune-armv8-2a                       = "lib64"
BASE_LIB_tune-armv8-2a-crypto                = "lib64"
