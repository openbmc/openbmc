#
# Defaults for ARMv8-m.base
#
DEFAULTTUNE ?= "armv8m-base"

TUNEVALID[armv8m-base] = "Enable instructions for ARMv8-m.base"
TUNE_CCARGS_MARCH .= "${@bb.utils.contains('TUNE_FEATURES', 'armv8m-base', ' -march=armv8-m.base', '', d)}"
MACHINEOVERRIDES =. "${@bb.utils.contains('TUNE_FEATURES', 'armv8m-base', 'armv8m-base:', '', d)}"

TUNECONFLICTS[armv8m-base] = "armv4 armv5 armv6 armv7a"

require conf/machine/include/arm/arch-armv7m.inc

AVAILTUNES                          += "armv8m-base"
ARMPKGARCH:tune-armv8m-base          = "armv8m-base"
TUNE_FEATURES:tune-armv8m-base       = "armv8m-base"
PACKAGE_EXTRA_ARCHS:tune-armv8m-base = "armv8m-base"
