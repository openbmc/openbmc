DEFAULTTUNE ?= "armv8-9a"

TUNEVALID[armv8-9a] = "Enable instructions for ARMv8.9-a"
TUNE_CCARGS_MARCH .= "${@bb.utils.contains('TUNE_FEATURES', 'armv8-9a', ' -march=armv8.9-a', '', d)}"
MACHINEOVERRIDES =. "${@bb.utils.contains('TUNE_FEATURES', 'armv8-9a', 'armv8-9a:', '', d)}"

require conf/machine/include/arm/arch-armv8a.inc

# Little Endian base configs
AVAILTUNES += "armv8-9a"
ARMPKGARCH:tune-armv8-9a                    ?= "armv8-9a"
TUNE_FEATURES:tune-armv8-9a                  = "aarch64 armv8-9a"
PACKAGE_EXTRA_ARCHS:tune-armv8-9a            = "${PACKAGE_EXTRA_ARCHS:tune-armv8a} armv8-9a"
BASE_LIB:tune-armv8-9a                       = "lib64"
