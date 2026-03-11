DEFAULTTUNE ?= "armv8-8a"

TUNEVALID[armv8-8a] = "Enable instructions for ARMv8.8-a"
TUNE_CCARGS_MARCH .= "${@bb.utils.contains('TUNE_FEATURES', 'armv8-8a', ' -march=armv8.8-a', '', d)}"
MACHINEOVERRIDES =. "${@bb.utils.contains('TUNE_FEATURES', 'armv8-8a', 'armv8-8a:', '', d)}"

require conf/machine/include/arm/arch-armv8a.inc

# Little Endian base configs
AVAILTUNES += "armv8-8a"
ARMPKGARCH:tune-armv8-8a                    ?= "armv8-8a"
TUNE_FEATURES:tune-armv8-8a                  = "aarch64 armv8-8a"
PACKAGE_EXTRA_ARCHS:tune-armv8-8a            = "${PACKAGE_EXTRA_ARCHS:tune-armv8a} armv8-8a"
BASE_LIB:tune-armv8-8a                       = "lib64"
