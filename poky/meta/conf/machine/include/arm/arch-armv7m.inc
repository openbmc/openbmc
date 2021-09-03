#
# Defaults for ARMv7-m
#
DEFAULTTUNE ?= "armv7m"

TUNEVALID[armv7m] = "Enable instructions for ARMv7-m"
TUNE_CCARGS_MARCH .= "${@bb.utils.contains('TUNE_FEATURES', 'armv7m', ' -march=armv7-m', '', d)}"
MACHINEOVERRIDES =. "${@bb.utils.contains('TUNE_FEATURES', 'armv7m', 'armv7m:', '', d)}"

TUNECONFLICTS[armv7m] = "armv4 armv5 armv6 armv7a"

require conf/machine/include/arm/arch-armv6m.inc

AVAILTUNES                            += "armv7m"
ARMPKGARCH:tune-armv7m                 = "armv7m"
TUNE_FEATURES:tune-armv7m              = "armv7m"
PACKAGE_EXTRA_ARCHS:tune-armv7m        = "armv7m"
