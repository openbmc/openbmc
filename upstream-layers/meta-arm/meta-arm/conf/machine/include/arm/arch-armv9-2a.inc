DEFAULTTUNE ?= "armv9-2a"

TUNEVALID[armv9-2a] = "Enable instructions for ARMv9.2-a"
TUNE_CCARGS_MARCH .= "${@bb.utils.contains('TUNE_FEATURES', 'armv9-2a', ' -march=armv9.2-a', '', d)}"
MACHINEOVERRIDES =. "${@bb.utils.contains('TUNE_FEATURES', 'armv9-2a', 'armv9-2a:', '', d)}"

require conf/machine/include/arm/arch-armv9a.inc

AVAILTUNES                                  += "armv9-2a"
ARMPKGARCH:tune-armv9-2a                    ?= "armv9-2a"
TUNE_FEATURES:tune-armv9-2a                  = "aarch64 armv9-2a"
PACKAGE_EXTRA_ARCHS:tune-armv9-2a            = "${PACKAGE_EXTRA_ARCHS:tune-armv9a} armv9-2a"
BASE_LIB:tune-armv9-2a                       = "lib64"
