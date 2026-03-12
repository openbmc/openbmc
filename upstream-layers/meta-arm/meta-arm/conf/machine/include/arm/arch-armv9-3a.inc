DEFAULTTUNE ?= "armv9-3a"

TUNEVALID[armv9-3a] = "Enable instructions for ARMv9.3-a"
TUNE_CCARGS_MARCH .= "${@bb.utils.contains('TUNE_FEATURES', 'armv9-3a', ' -march=armv9.3-a', '', d)}"
MACHINEOVERRIDES =. "${@bb.utils.contains('TUNE_FEATURES', 'armv9-3a', 'armv9-3a:', '', d)}"

require conf/machine/include/arm/arch-armv9a.inc

AVAILTUNES                                  += "armv9-3a"
ARMPKGARCH:tune-armv9-3a                    ?= "armv9-3a"
TUNE_FEATURES:tune-armv9-3a                  = "aarch64 armv9-3a"
PACKAGE_EXTRA_ARCHS:tune-armv9-3a            = "${PACKAGE_EXTRA_ARCHS:tune-armv9a} armv9-3a"
BASE_LIB:tune-armv9-3a                       = "lib64"
