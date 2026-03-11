DEFAULTTUNE ?= "armv9-4a"

TUNEVALID[armv9-4a] = "Enable instructions for ARMv9.4-a"
TUNE_CCARGS_MARCH .= "${@bb.utils.contains('TUNE_FEATURES', 'armv9-4a', ' -march=armv9.4-a', '', d)}"
MACHINEOVERRIDES =. "${@bb.utils.contains('TUNE_FEATURES', 'armv9-4a', 'armv9-4a:', '', d)}"

require conf/machine/include/arm/arch-armv9a.inc

AVAILTUNES                                  += "armv9-4a"
ARMPKGARCH:tune-armv9-4a                    ?= "armv9-4a"
TUNE_FEATURES:tune-armv9-4a                  = "aarch64 armv9-4a"
PACKAGE_EXTRA_ARCHS:tune-armv9-4a            = "${PACKAGE_EXTRA_ARCHS:tune-armv8a} armv9-4a"
BASE_LIB:tune-armv9-4a                       = "lib64"
