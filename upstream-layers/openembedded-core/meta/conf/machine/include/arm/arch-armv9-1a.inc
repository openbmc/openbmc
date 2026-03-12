DEFAULTTUNE ?= "armv9-1a"

TUNEVALID[armv9-1a] = "Enable instructions for ARMv9.1-a"
TUNE_CCARGS_MARCH .= "${@bb.utils.contains('TUNE_FEATURES', 'armv9-1a', ' -march=armv9.1-a', '', d)}"
MACHINEOVERRIDES =. "${@bb.utils.contains('TUNE_FEATURES', 'armv9-1a', 'armv9-1a:', '', d)}"

require conf/machine/include/arm/arch-armv9a.inc

AVAILTUNES                                  += "armv9-1a"
ARMPKGARCH:tune-armv9-1a                    ?= "armv9-1a"
TUNE_FEATURES:tune-armv9-1a                  = "aarch64 armv9-1a"
PACKAGE_EXTRA_ARCHS:tune-armv9-1a            = "${PACKAGE_EXTRA_ARCHS:tune-armv9a} armv9-1a"
BASE_LIB:tune-armv9-1a                       = "lib64"
