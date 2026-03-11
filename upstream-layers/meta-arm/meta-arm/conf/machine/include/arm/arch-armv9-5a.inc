DEFAULTTUNE ?= "armv9-5a"

TUNEVALID[armv9-5a] = "Enable instructions for ARMv9.5-a"
TUNE_CCARGS_MARCH .= "${@bb.utils.contains('TUNE_FEATURES', 'armv9-5a', ' -march=armv9.5-a', '', d)}"
MACHINEOVERRIDES =. "${@bb.utils.contains('TUNE_FEATURES', 'armv9-5a', 'armv9-5a:', '', d)}"

require conf/machine/include/arm/arch-armv9a.inc

AVAILTUNES                                  += "armv9-5a"
ARMPKGARCH:tune-armv9-5a                    ?= "armv9-5a"
TUNE_FEATURES:tune-armv9-5a                  = "aarch64 armv9-5a"
PACKAGE_EXTRA_ARCHS:tune-armv9-5a            = "${PACKAGE_EXTRA_ARCHS:tune-armv8a} armv9-5a"
BASE_LIB:tune-armv9-5a                       = "lib64"
